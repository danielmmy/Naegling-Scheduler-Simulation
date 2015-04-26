package com.danielmmy;

import com.danielmmy.Task.TaskType;

public class TaskInstance {
	
	private int mConsumedCores;
	private int mConsumedRam;
	private Task mTask;
	
	public TaskInstance(Task task,int consumedCores,int consumedRam) {
		mConsumedCores=consumedCores;
		mConsumedRam=consumedRam;
		mTask=task;
	}

	public int getmConsumedCores() {
		return mConsumedCores;
	}

	public int getmConsumedRam() {
		return mConsumedRam;
	}
	
	public void addProcessTime(double affinity){
		mTask.addProcessTime(affinity);
	}
	
	public TaskType getType(){
		return mTask.getmTaskType();
	}
	
	public boolean belongsTo(Task task){
		return mTask.getTaskId()==task.getTaskId();
			
	}
}
