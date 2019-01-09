package edu.kit.ifv.mobitopp.matsim;

import edu.kit.ifv.mobitopp.data.Network;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public class MatsimContext extends SimulationContextDecorator implements SimulationContext {

	private final Network network;
  private ImpedanceIfc currentImpedance;

	public MatsimContext(SimulationContext baseContext, Network network) {
		super(baseContext);
		this.network = network;
		currentImpedance = baseContext.impedance();
	}

	public VisumNetwork network() {
		return network.visumNetwork;
	}
	
	@Override
	public ImpedanceIfc impedance() {
	  return currentImpedance;
	}

  public void updateImpedance(ImpedanceIfc newImpedance) {
    this.currentImpedance = newImpedance;
  }

}
