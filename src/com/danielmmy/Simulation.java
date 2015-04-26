package com.danielmmy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.danielmmy.Scheduler.AffinityType;
import com.danielmmy.Task.TaskType;

public class Simulation {

	public static void main(String[] args) {
		for(int l=0;l<30;++l){
			
			/*
			 * creating affinity vector, will not be altered during each test execution. Can be the same for all schedulers
			 */
			double[][][] AFFINITY3=new double[Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER];
			Random r=new Random();
			double affinity;
			for(int i=0;i<Task.TYPES_OF_TASK_NUMBER;++i){
				for(int j=0;j<Task.TYPES_OF_TASK_NUMBER;++j){
					for(int k=0;k<Task.TYPES_OF_TASK_NUMBER;++k){
						do{
							affinity=r.nextDouble();
						}while(affinity==0);
						AFFINITY3[i][j][k]=AFFINITY3[i][k][j]=AFFINITY3[k][i][j]=AFFINITY3[j][i][k]=AFFINITY3[k][j][i]=AFFINITY3[j][k][i]=affinity;	
					}
				}
			}
			
			/*
			 * Creates identical infrastructure for each scheduler
			 */
			List<Node> fifoInfrastructure=new ArrayList<>();
			List<Node> firstAvailableInfrastructure=new ArrayList<>();
			List<Node> roundRobinInfrastructure=new ArrayList<>();
			List<Node> affinityAwareInfrastructure15=new ArrayList<>();
			List<Node> affinityAwareInfrastructure3000=new ArrayList<>();
			for(int i=0; i<Scheduler.TEST_NUMBER_OF_NODES;++i){
				fifoInfrastructure.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				firstAvailableInfrastructure.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				roundRobinInfrastructure.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				affinityAwareInfrastructure15.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				affinityAwareInfrastructure3000.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
			}
			
			/*
			 * Creates identical tasks for each scheduler
			 */
			List<Task> fifoTasks=new ArrayList<>();
			List<Task> firstAvailableTasks=new ArrayList<>();
			List<Task> roundRobinTasks=new ArrayList<>();
			List<Task> affinityAwareTasks15=new ArrayList<>();
			List<Task> affinityAwareTasks3000=new ArrayList<>();
			TaskType[] types=TaskType.values();
			for(int i=0;i<Scheduler.TEST_NUMBER_OF_PROCESS;++i){
				TaskType type=types[r.nextInt(Task.TYPES_OF_TASK_NUMBER)];
				int instanceNumber=r.nextInt(101)+10;//from 10 to 100 instances
				int coresPerInstance=r.nextInt(12)+1;//from 1 to 12
				int ramPerinstance=r.nextInt(12*1024+101)+100;//from 100MB to 12GB
				double baseTime=r.nextInt(10001)+1000;//from 100 cycles to 1000
				fifoTasks.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				firstAvailableTasks.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				roundRobinTasks.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				affinityAwareTasks15.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				affinityAwareTasks3000.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
			}
			
			/*
			 * Creates identical arrival order for each scheduler
			 */
			List<Integer> fifoArrivalOrder=new ArrayList<>();
			List<Integer> firstAvailableArrivalOrder=new ArrayList<>();
			List<Integer> roundRobinArrivalOrder=new ArrayList<>();
			List<Integer> affinityAwareArrivalOrder15=new ArrayList<>();
			List<Integer> affinityAwareArrivalOrder3000=new ArrayList<>();
			int tasksSoFar=0;
			do{
				tasksSoFar+=r.nextInt(11);
				fifoArrivalOrder.add((tasksSoFar<fifoTasks.size())?tasksSoFar:fifoTasks.size());
				firstAvailableArrivalOrder.add((tasksSoFar<firstAvailableTasks.size())?tasksSoFar:firstAvailableTasks.size());
				roundRobinArrivalOrder.add((tasksSoFar<roundRobinTasks.size())?tasksSoFar:roundRobinTasks.size());
				affinityAwareArrivalOrder15.add((tasksSoFar<affinityAwareTasks15.size())?tasksSoFar:affinityAwareTasks15.size());
				affinityAwareArrivalOrder3000.add((tasksSoFar<affinityAwareTasks3000.size())?tasksSoFar:affinityAwareTasks3000.size());
			}while(tasksSoFar<fifoTasks.size());
			
			/*
			 * Creates the schedulers
			 */
			final Scheduler fifoScheduler=new FifoScheduler(fifoTasks, fifoArrivalOrder, fifoInfrastructure, AFFINITY3,AffinityType.NORMAL);
			final Scheduler firstAvailableScheduler=new FirstAvailableScheduler(firstAvailableTasks, firstAvailableArrivalOrder, firstAvailableInfrastructure, AFFINITY3, AffinityType.NORMAL);
			final Scheduler roundRobinScheduler=new RoundRobinScheduler(roundRobinTasks, roundRobinArrivalOrder, roundRobinInfrastructure, AFFINITY3, AffinityType.NORMAL);
			final Scheduler affinityAwareScheduler15=new AffinityAwareScheduler(affinityAwareTasks15, affinityAwareArrivalOrder15, affinityAwareInfrastructure15, AFFINITY3, AffinityType.NORMAL,15);
			final Scheduler affinityAwareScheduler3000=new AffinityAwareScheduler(affinityAwareTasks3000, affinityAwareArrivalOrder3000, affinityAwareInfrastructure3000, AFFINITY3, AffinityType.NORMAL,3000);
			
			/*
			 * Creates a thread for each scheduler
			 */
			Thread fifoThread=new Thread(new Runnable() {		
				@Override
				public void run() {
					fifoScheduler.runScheduler();

				}
			});
			Thread firstAvailableThread=new Thread(new Runnable() {		
				@Override
				public void run() {
					firstAvailableScheduler.runScheduler();

				}
			});
			Thread roundRobinThread=new Thread(new Runnable() {		
				@Override
				public void run() {
					roundRobinScheduler.runScheduler();

				}
			});
			Thread affinityAwareThread15=new Thread(new Runnable() {		
				@Override
				public void run() {
					affinityAwareScheduler15.runScheduler();

				}
			});
			Thread affinityAwareThread3000=new Thread(new Runnable() {		
				@Override
				public void run() {
					affinityAwareScheduler3000.runScheduler();

				}
			});
			
			/*
			 * Start each thread
			 */
			fifoThread.start();
			firstAvailableThread.start();
			roundRobinThread.start();
			affinityAwareThread15.start();
			affinityAwareThread3000.start();
			
			/*
			 * Wait for all threads
			 */
			try {
				fifoThread.join();
				firstAvailableThread.join();
				roundRobinThread.join();
				affinityAwareThread15.join();
				affinityAwareThread3000.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Finished running "+(l+1)+" iterations");
		}
	}
}
