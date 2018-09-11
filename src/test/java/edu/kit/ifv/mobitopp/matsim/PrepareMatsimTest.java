package edu.kit.ifv.mobitopp.matsim;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Test;
import org.matsim.core.config.Config;

import edu.kit.ifv.mobitopp.data.Network;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.simulation.WrittenConfiguration;

public class PrepareMatsimTest {

	private static final String baseFolder = "results";

	@Test
	public void updateOutputFolder() {
		Config config = createMatsimConfig();
		MatsimContext matsimContext = createContext();
		PrepareMatsim prepareMatsim = new PrepareMatsim(matsimContext);

		prepareMatsim.updateResultFolder(config);

		String matsimOutput = new File(baseFolder, PrepareMatsim.resultFolder).getAbsolutePath();
		assertThat(config.controler().getOutputDirectory(), is(equalTo(matsimOutput)));
	}

	private MatsimContext createContext() {
		Network network = mock(Network.class);
		SimulationContext context = mock(SimulationContext.class);
		WrittenConfiguration configuration = new WrittenConfiguration();
		configuration.setResultFolder(baseFolder);
		when(context.configuration()).thenReturn(configuration);
		return new MatsimContext(context, network);
	}

	private Config createMatsimConfig() {
		Config config = new Config();
		config.addCoreModules();
		return config;
	}
}
