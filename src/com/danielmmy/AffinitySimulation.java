package com.danielmmy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.danielmmy.Scheduler.AffinityType;
import com.danielmmy.Task.TaskType;

public class AffinitySimulation {
	
	public static void main(String[] args) {
		for(int l=0;l<100;++l){
			
			/*
			 * creating normal affinity vector, will not be altered during each test execution. Can be the same for all normal affinity schedulers
			 */
			double[][][] NORMAL_AFFINITY3=new double[Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER];
			Random r=new Random();
			double affinity;
			for(int i=0;i<Task.TYPES_OF_TASK_NUMBER;++i){
				for(int j=0;j<Task.TYPES_OF_TASK_NUMBER;++j){
					for(int k=0;k<Task.TYPES_OF_TASK_NUMBER;++k){
						do{
							affinity=r.nextDouble();
						}while(affinity==0);
						NORMAL_AFFINITY3[i][j][k]=NORMAL_AFFINITY3[i][k][j]=NORMAL_AFFINITY3[k][i][j]=NORMAL_AFFINITY3[j][i][k]=NORMAL_AFFINITY3[k][j][i]=NORMAL_AFFINITY3[j][k][i]=affinity;	
					}
				}
			}
			
			/*
			 * creating low affinity vector, will not be altered during each test execution. Can be the same for all low affinity schedulers
			 */
			double[][][] LOW_AFFINITY3=new double[Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER];
			for(int i=0;i<Task.TYPES_OF_TASK_NUMBER;++i){
				for(int j=0;j<Task.TYPES_OF_TASK_NUMBER;++j){
					for(int k=0;k<Task.TYPES_OF_TASK_NUMBER;++k){
						do{
							affinity=r.nextDouble();
						}while(affinity==0||affinity>0.6);
						LOW_AFFINITY3[i][j][k]=LOW_AFFINITY3[i][k][j]=LOW_AFFINITY3[k][i][j]=LOW_AFFINITY3[j][i][k]=LOW_AFFINITY3[k][j][i]=LOW_AFFINITY3[j][k][i]=affinity;	
					}
				}
			}
			
			/*
			 * creating high affinity vector, will not be altered during each test execution. Can be the same for all high affinity schedulers
			 */
			double[][][] HIGH_AFFINITY3=new double[Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER][Task.TYPES_OF_TASK_NUMBER];
			for(int i=0;i<Task.TYPES_OF_TASK_NUMBER;++i){
				for(int j=0;j<Task.TYPES_OF_TASK_NUMBER;++j){
					for(int k=0;k<Task.TYPES_OF_TASK_NUMBER;++k){
						do{
							affinity=r.nextDouble();
						}while(affinity<0.4);
						HIGH_AFFINITY3[i][j][k]=HIGH_AFFINITY3[i][k][j]=HIGH_AFFINITY3[k][i][j]=HIGH_AFFINITY3[j][i][k]=HIGH_AFFINITY3[k][j][i]=HIGH_AFFINITY3[j][k][i]=affinity;	
					}
				}
			}
			
			/*
			 * Creates identical infrastructure for each scheduler
			 */
			List<Node> fifoInfrastructureNormal=new ArrayList<>();
			List<Node> firstAvailableInfrastructureNormal=new ArrayList<>();
			List<Node> roundRobinInfrastructureNormal=new ArrayList<>();
			List<Node> affinityAwareInfrastructure15Normal=new ArrayList<>();
			List<Node> affinityAwareInfrastructure3000Normal=new ArrayList<>();
			List<Node> fifoInfrastructureLow=new ArrayList<>();
			List<Node> firstAvailableInfrastructureLow=new ArrayList<>();
			List<Node> roundRobinInfrastructureLow=new ArrayList<>();
			List<Node> affinityAwareInfrastructure15Low=new ArrayList<>();
			List<Node> affinityAwareInfrastructure3000Low=new ArrayList<>();
			List<Node> fifoInfrastructureHigh=new ArrayList<>();
			List<Node> firstAvailableInfrastructureHigh=new ArrayList<>();
			List<Node> roundRobinInfrastructureHigh=new ArrayList<>();
			List<Node> affinityAwareInfrastructure15High=new ArrayList<>();
			List<Node> affinityAwareInfrastructure3000High=new ArrayList<>();
			for(int i=0; i<Scheduler.TEST_NUMBER_OF_NODES;++i){
				fifoInfrastructureNormal.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				firstAvailableInfrastructureNormal.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				roundRobinInfrastructureNormal.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				affinityAwareInfrastructure15Normal.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				affinityAwareInfrastructure3000Normal.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				fifoInfrastructureLow.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				firstAvailableInfrastructureLow.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				roundRobinInfrastructureLow.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				affinityAwareInfrastructure15Low.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				affinityAwareInfrastructure3000Low.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				fifoInfrastructureHigh.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				firstAvailableInfrastructureHigh.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				roundRobinInfrastructureHigh.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				affinityAwareInfrastructure15High.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
				affinityAwareInfrastructure3000High.add(new Node(Scheduler.TEST_NUMBER_OF_CORES, Scheduler.TEST_RAM_SIZE));
			}
			
			/*
			 * Creates identical tasks for each scheduler
			 */
			List<Task> fifoTasksNormal=new ArrayList<>();
			List<Task> firstAvailableTasksNormal=new ArrayList<>();
			List<Task> roundRobinTasksNormal=new ArrayList<>();
			List<Task> affinityAwareTasks15Normal=new ArrayList<>();
			List<Task> affinityAwareTasks3000Normal=new ArrayList<>();
			List<Task> fifoTasksLow=new ArrayList<>();
			List<Task> firstAvailableTasksLow=new ArrayList<>();
			List<Task> roundRobinTasksLow=new ArrayList<>();
			List<Task> affinityAwareTasks15Low=new ArrayList<>();
			List<Task> affinityAwareTasks3000Low=new ArrayList<>();
			List<Task> fifoTasksHigh=new ArrayList<>();
			List<Task> firstAvailableTasksHigh=new ArrayList<>();
			List<Task> roundRobinTasksHigh=new ArrayList<>();
			List<Task> affinityAwareTasks15High=new ArrayList<>();
			List<Task> affinityAwareTasks3000High=new ArrayList<>();
			TaskType[] types=TaskType.values();
			for(int i=0;i<Scheduler.TEST_NUMBER_OF_PROCESS;++i){
				TaskType type=types[r.nextInt(Task.TYPES_OF_TASK_NUMBER)];
				int instanceNumber=r.nextInt(101)+10;//from 10 to 100 instances
				int coresPerInstance=r.nextInt(12)+1;//from 1 to 12
				int ramPerinstance=r.nextInt(12*1024+101)+100;//from 100MB to 12GB
				double baseTime=r.nextInt(10001)+1000;//from 100 cycles to 1000
				fifoTasksNormal.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				firstAvailableTasksNormal.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				roundRobinTasksNormal.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				affinityAwareTasks15Normal.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				affinityAwareTasks3000Normal.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				fifoTasksLow.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				firstAvailableTasksLow.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				roundRobinTasksLow.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				affinityAwareTasks15Low.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				affinityAwareTasks3000Low.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				fifoTasksHigh.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				firstAvailableTasksHigh.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				roundRobinTasksHigh.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				affinityAwareTasks15High.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
				affinityAwareTasks3000High.add(new Task(type,instanceNumber,coresPerInstance,ramPerinstance,baseTime));
			}
			
			/*
			 * Creates identical arrival order for each scheduler
			 */
			List<Integer> fifoArrivalOrderNormal=new ArrayList<>();
			List<Integer> firstAvailableArrivalOrderNormal=new ArrayList<>();
			List<Integer> roundRobinArrivalOrderNormal=new ArrayList<>();
			List<Integer> affinityAwareArrivalOrder15Normal=new ArrayList<>();
			List<Integer> affinityAwareArrivalOrder3000Normal=new ArrayList<>();
			List<Integer> fifoArrivalOrderLow=new ArrayList<>();
			List<Integer> firstAvailableArrivalOrderLow=new ArrayList<>();
			List<Integer> roundRobinArrivalOrderLow=new ArrayList<>();
			List<Integer> affinityAwareArrivalOrder15Low=new ArrayList<>();
			List<Integer> affinityAwareArrivalOrder3000Low=new ArrayList<>();
			List<Integer> fifoArrivalOrderHigh=new ArrayList<>();
			List<Integer> firstAvailableArrivalOrderHigh=new ArrayList<>();
			List<Integer> roundRobinArrivalOrderHigh=new ArrayList<>();
			List<Integer> affinityAwareArrivalOrder15High=new ArrayList<>();
			List<Integer> affinityAwareArrivalOrder3000High=new ArrayList<>();
			int tasksSoFar=0;
			do{
				tasksSoFar+=r.nextInt(11);
				fifoArrivalOrderNormal.add((tasksSoFar<fifoTasksNormal.size())?tasksSoFar:fifoTasksNormal.size());
				firstAvailableArrivalOrderNormal.add((tasksSoFar<firstAvailableTasksNormal.size())?tasksSoFar:firstAvailableTasksNormal.size());
				roundRobinArrivalOrderNormal.add((tasksSoFar<roundRobinTasksNormal.size())?tasksSoFar:roundRobinTasksNormal.size());
				affinityAwareArrivalOrder15Normal.add((tasksSoFar<affinityAwareTasks15Normal.size())?tasksSoFar:affinityAwareTasks15Normal.size());
				affinityAwareArrivalOrder3000Normal.add((tasksSoFar<affinityAwareTasks3000Normal.size())?tasksSoFar:affinityAwareTasks3000Normal.size());
				fifoArrivalOrderLow.add((tasksSoFar<fifoTasksLow.size())?tasksSoFar:fifoTasksLow.size());
				firstAvailableArrivalOrderLow.add((tasksSoFar<firstAvailableTasksLow.size())?tasksSoFar:firstAvailableTasksLow.size());
				roundRobinArrivalOrderLow.add((tasksSoFar<roundRobinTasksLow.size())?tasksSoFar:roundRobinTasksLow.size());
				affinityAwareArrivalOrder15Low.add((tasksSoFar<affinityAwareTasks15Low.size())?tasksSoFar:affinityAwareTasks15Low.size());
				affinityAwareArrivalOrder3000Low.add((tasksSoFar<affinityAwareTasks3000Low.size())?tasksSoFar:affinityAwareTasks3000Low.size());
				fifoArrivalOrderHigh.add((tasksSoFar<fifoTasksHigh.size())?tasksSoFar:fifoTasksHigh.size());
				firstAvailableArrivalOrderHigh.add((tasksSoFar<firstAvailableTasksHigh.size())?tasksSoFar:firstAvailableTasksHigh.size());
				roundRobinArrivalOrderHigh.add((tasksSoFar<roundRobinTasksHigh.size())?tasksSoFar:roundRobinTasksHigh.size());
				affinityAwareArrivalOrder15High.add((tasksSoFar<affinityAwareTasks15High.size())?tasksSoFar:affinityAwareTasks15High.size());
				affinityAwareArrivalOrder3000High.add((tasksSoFar<affinityAwareTasks3000High.size())?tasksSoFar:affinityAwareTasks3000High.size());
			}while(tasksSoFar<fifoTasksHigh.size());
			
			/*
			 * Creates the schedulers
			 */
			final Scheduler fifoSchedulerNormal=new FifoScheduler(fifoTasksNormal, fifoArrivalOrderNormal, fifoInfrastructureNormal, NORMAL_AFFINITY3, AffinityType.NORMAL);
			final Scheduler firstAvailableSchedulerNormal=new FirstAvailableScheduler(firstAvailableTasksNormal, firstAvailableArrivalOrderNormal, firstAvailableInfrastructureNormal, NORMAL_AFFINITY3, AffinityType.NORMAL);
			final Scheduler roundRobinSchedulerNormal=new RoundRobinScheduler(roundRobinTasksNormal, roundRobinArrivalOrderNormal, roundRobinInfrastructureNormal, NORMAL_AFFINITY3, AffinityType.NORMAL);
			final Scheduler affinityAwareScheduler15Normal=new AffinityAwareScheduler(affinityAwareTasks15Normal, affinityAwareArrivalOrder15Normal, affinityAwareInfrastructure15Normal, NORMAL_AFFINITY3, AffinityType.NORMAL,15);
			final Scheduler affinityAwareScheduler3000Normal=new AffinityAwareScheduler(affinityAwareTasks3000Normal, affinityAwareArrivalOrder3000Normal, affinityAwareInfrastructure3000Normal, NORMAL_AFFINITY3, AffinityType.NORMAL,3000);
			final Scheduler fifoSchedulerLow=new FifoScheduler(fifoTasksLow, fifoArrivalOrderLow, fifoInfrastructureLow, LOW_AFFINITY3, AffinityType.LOW);
			final Scheduler firstAvailableSchedulerLow=new FirstAvailableScheduler(firstAvailableTasksLow, firstAvailableArrivalOrderLow, firstAvailableInfrastructureLow, LOW_AFFINITY3, AffinityType.LOW);
			final Scheduler roundRobinSchedulerLow=new RoundRobinScheduler(roundRobinTasksLow, roundRobinArrivalOrderLow, roundRobinInfrastructureLow, LOW_AFFINITY3, AffinityType.LOW);
			final Scheduler affinityAwareScheduler15Low=new AffinityAwareScheduler(affinityAwareTasks15Low, affinityAwareArrivalOrder15Low, affinityAwareInfrastructure15Low, LOW_AFFINITY3, AffinityType.LOW,15);
			final Scheduler affinityAwareScheduler3000Low=new AffinityAwareScheduler(affinityAwareTasks3000Low, affinityAwareArrivalOrder3000Low, affinityAwareInfrastructure3000Low, LOW_AFFINITY3, AffinityType.LOW,3000);
			final Scheduler fifoSchedulerHigh=new FifoScheduler(fifoTasksHigh, fifoArrivalOrderHigh, fifoInfrastructureHigh, HIGH_AFFINITY3, AffinityType.HIGH);
			final Scheduler firstAvailableSchedulerHigh=new FirstAvailableScheduler(firstAvailableTasksHigh, firstAvailableArrivalOrderHigh, firstAvailableInfrastructureHigh, HIGH_AFFINITY3, AffinityType.HIGH);
			final Scheduler roundRobinSchedulerHigh=new RoundRobinScheduler(roundRobinTasksHigh, roundRobinArrivalOrderHigh, roundRobinInfrastructureHigh, HIGH_AFFINITY3, AffinityType.HIGH);
			final Scheduler affinityAwareScheduler15High=new AffinityAwareScheduler(affinityAwareTasks15High, affinityAwareArrivalOrder15High, affinityAwareInfrastructure15High, HIGH_AFFINITY3, AffinityType.HIGH,15);
			final Scheduler affinityAwareScheduler3000High=new AffinityAwareScheduler(affinityAwareTasks3000High, affinityAwareArrivalOrder3000High, affinityAwareInfrastructure3000High, HIGH_AFFINITY3, AffinityType.HIGH,3000);
			
			/*
			 * Creates a thread for each Normal scheduler
			 */
			Thread fifoThreadNormal=new Thread(new Runnable() {		
				@Override
				public void run() {
					fifoSchedulerNormal.runScheduler();

				}
			});
			Thread firstAvailableThreadNormal=new Thread(new Runnable() {		
				@Override
				public void run() {
					firstAvailableSchedulerNormal.runScheduler();

				}
			});
			Thread roundRobinThreadNormal=new Thread(new Runnable() {		
				@Override
				public void run() {
					roundRobinSchedulerNormal.runScheduler();

				}
			});
			Thread affinityAwareThread15Normal=new Thread(new Runnable() {		
				@Override
				public void run() {
					affinityAwareScheduler15Normal.runScheduler();

				}
			});
			Thread affinityAwareThread3000Normal=new Thread(new Runnable() {		
				@Override
				public void run() {
					affinityAwareScheduler3000Normal.runScheduler();

				}
			});
			
			/*
			 * Start each Normal thread
			 */
			fifoThreadNormal.start();
			firstAvailableThreadNormal.start();
			roundRobinThreadNormal.start();
			affinityAwareThread15Normal.start();
			affinityAwareThread3000Normal.start();
			
			/*
			 * Wait for all threads
			 */
			try {
				fifoThreadNormal.join();
				firstAvailableThreadNormal.join();
				roundRobinThreadNormal.join();
				affinityAwareThread15Normal.join();
				affinityAwareThread3000Normal.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			/*
			 * Creates a thread for each Low scheduler
			 */
			Thread fifoThreadLow=new Thread(new Runnable() {		
				@Override
				public void run() {
					fifoSchedulerLow.runScheduler();

				}
			});
			Thread firstAvailableThreadLow=new Thread(new Runnable() {		
				@Override
				public void run() {
					firstAvailableSchedulerLow.runScheduler();

				}
			});
			Thread roundRobinThreadLow=new Thread(new Runnable() {		
				@Override
				public void run() {
					roundRobinSchedulerLow.runScheduler();

				}
			});
			Thread affinityAwareThread15Low=new Thread(new Runnable() {		
				@Override
				public void run() {
					affinityAwareScheduler15Low.runScheduler();

				}
			});
			Thread affinityAwareThread3000Low=new Thread(new Runnable() {		
				@Override
				public void run() {
					affinityAwareScheduler3000Low.runScheduler();

				}
			});
			
			/*
			 * Start each Low thread
			 */
			fifoThreadLow.start();
			firstAvailableThreadLow.start();
			roundRobinThreadLow.start();
			affinityAwareThread15Low.start();
			affinityAwareThread3000Low.start();
			
			/*
			 * Wait for all Low threads
			 */
			try {
				fifoThreadLow.join();
				firstAvailableThreadLow.join();
				roundRobinThreadLow.join();
				affinityAwareThread15Low.join();
				affinityAwareThread3000Low.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			/*
			 * Creates a thread for each High scheduler
			 */
			Thread fifoThreadHigh=new Thread(new Runnable() {		
				@Override
				public void run() {
					fifoSchedulerHigh.runScheduler();

				}
			});
			Thread firstAvailableThreadHigh=new Thread(new Runnable() {		
				@Override
				public void run() {
					firstAvailableSchedulerHigh.runScheduler();

				}
			});
			Thread roundRobinThreadHigh=new Thread(new Runnable() {		
				@Override
				public void run() {
					roundRobinSchedulerHigh.runScheduler();

				}
			});
			Thread affinityAwareThread15High=new Thread(new Runnable() {		
				@Override
				public void run() {
					affinityAwareScheduler15High.runScheduler();

				}
			});
			Thread affinityAwareThread3000High=new Thread(new Runnable() {		
				@Override
				public void run() {
					affinityAwareScheduler3000High.runScheduler();

				}
			});
			
			/*
			 * Start each High thread
			 */
			fifoThreadHigh.start();
			firstAvailableThreadHigh.start();
			roundRobinThreadHigh.start();
			affinityAwareThread15High.start();
			affinityAwareThread3000High.start();
			
			/*
			 * Wait for all High threads
			 */
			try {
				fifoThreadHigh.join();
				firstAvailableThreadHigh.join();
				roundRobinThreadHigh.join();
				affinityAwareThread15High.join();
				affinityAwareThread3000High.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Finished running "+(l+1)+" iterations");
		}
	}

}
