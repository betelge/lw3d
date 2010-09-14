package lw3d;

import java.util.Iterator;
import java.util.Set;

import org.lwjgl.Sys;

import lw3d.renderer.Node;

public class Lw3dSimulator {

	private Lw3dSimulation simulation;

	private Set<Node> nodes;

	private long time;
	private int timeAccumulator = 0;

	// Time passed to the simulation. Doesn't increase while the sim is paused.
	private long simTime = 0;

	private SimState simState = SimState.STOP;

	public void setSimulation(Lw3dSimulation simulation) {
		this.simulation = simulation;
	}

	public enum SimState {
		STOP, RUN, PAUSE, EXIT;
	}

	Thread simulatorThread;
	private Runnable simulatorRunnable = new Runnable() {
		@Override
		public void run() {

			time = Sys.getTime();
			while (simState != SimState.EXIT) {

				while (simState == SimState.STOP)
					Thread.yield();

				long newTime = Sys.getTime();
				timeAccumulator += newTime - time;
				time = newTime;

				if (simulation != null && simState == SimState.RUN) {
					// TODO: check if the sim runs slow
					long timeStep = simulation.getTimeStep();
					if (timeAccumulator >= timeStep) {
						timeAccumulator -= timeStep;
						
						simulation.setTime(simTime);
						simulation.setRealTime(time - timeAccumulator);

						simulation.beforeProcessingNodes();

						Iterator<Node> it = nodes.iterator();
						while (it.hasNext()) {
							Node node = it.next();
							simulation.preProcessNode(node);
							simulation.processNode(node);
						}
						
						simTime += timeStep;
					}
				}

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	public Lw3dSimulator(Set<Node> nodes) {
		this.nodes = nodes;
		System.out.println(nodes);
		simulatorThread = new Thread(simulatorRunnable, "simulatorThread");
		simulatorThread.start();
	}

	public void start() {
		time = Sys.getTime();
		simState = SimState.RUN;
	}

	public void exit() {
		simState = SimState.EXIT;
	}

	public Lw3dSimulation getSimulation() {
		return simulation;
	}

}
