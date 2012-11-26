package org.backmeup.job;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import akka.util.Duration;

import models.Backup;
import models.Job;

import play.Logger;
import play.libs.Akka;

public abstract class JobManager {
	
	public void start() {
		Logger.info("Starting up job manager");
		
		// TODO only take N next recent ones (at least if allJobs has an excessive length)
		for (Job j : Job.find.all()) {
			Job storedJob = Job.findById(j.id);
			if (storedJob != null) {
				queueJob(storedJob);
			}
		}
	}
	
	public void queueJob(Job job) {
		// Compute next job execution time
		long currentTime = new Date().getTime();
		long executeIn = job.start.getTime() - currentTime;
		
		// If job execution was scheduled for within the past 5 mins, still schedule now...
		if (executeIn >= -300000 && executeIn < 0)
			executeIn = 0;
		
		// ...otherwise, schedule on the next occasion defined by .getStart and .getDelay
		if (executeIn < 0)
			executeIn += Math.ceil((double) Math.abs(executeIn) / (double) job.delay) * job.delay;			      			      
    
		Akka.system().scheduler().scheduleOnce(
			Duration.create(executeIn, TimeUnit.MILLISECONDS), // Initial delay
			new RunRescheduleLoop(job));	
	}
	
	protected abstract void executeBackup(Backup backup) throws JobException;
	
	private class RunRescheduleLoop implements Runnable {
		
		private Job job;
		
		RunRescheduleLoop(Job job) {
			this.job = job;
		}
		
		@Override
		public void run() {
			// Might have been deleted in the mean time
			if (Job.findById(job.id) != null) {		
				Backup backup = new Backup(job, Backup.Status.RUNNING, new Date(), null);
				backup.save();
				
				try {	
					// Actual Job execution happens in subclasses of JobManager
					executeBackup(backup);
					backup.endTime = new Date();
					backup.status = Backup.Status.SUCCESS;
					backup.update();
					
					// Reschedule if it's still in the DB
					Job nextJob = Job.findById(job.id);
					if (nextJob != null) {
						Logger.info("Rescheduling job for execution in " + job.delay + "ms");
						Akka.system().scheduler().scheduleOnce(
								Duration.create(job.delay, TimeUnit.MILLISECONDS), 
								new RunRescheduleLoop(job));
					} else {
						Logger.info("Job deleted in the mean time - no re-scheduling.");
					}
				} catch (JobException e) {
					backup.endTime = new Date();
					backup.status = Backup.Status.ERROR;
					backup.update();
					
					Logger.error("Job was terminated violently - no re-scheduling.");
					e.printStackTrace();
				}
			} else {
				Logger.info("Job deleted in the mean time - no execution/rescheduling.");
			}
		}
	}

}