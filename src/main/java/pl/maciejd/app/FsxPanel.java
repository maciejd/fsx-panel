package pl.maciejd.app;

import flightsim.simconnect.NotificationPriority;
import flightsim.simconnect.SimConnect;
import flightsim.simconnect.SimConnectConstants;
import flightsim.simconnect.SimConnectDataType;
import flightsim.simconnect.SimConnectPeriod;
import flightsim.simconnect.recv.AbstractDispatcher;
import flightsim.simconnect.recv.DispatcherTask;
import flightsim.simconnect.recv.EventHandler;
import flightsim.simconnect.recv.ExceptionHandler;
import flightsim.simconnect.recv.OpenHandler;
import flightsim.simconnect.recv.RecvEvent;
import flightsim.simconnect.recv.RecvException;
import flightsim.simconnect.recv.RecvOpen;
import flightsim.simconnect.recv.RecvPacket;
import flightsim.simconnect.recv.RecvSimObjectData;
import flightsim.simconnect.recv.SimObjectDataHandler;

public class FsxPanel {// implements EventHandler, OpenHandler {

	int newAirspeed, oldAirspeed;

	enum EVENT_ID {
		KEY_PANEL_LIGHTS_TOGGLE, KEY_TOGGLE_AVIONICS_MASTER,
	};

	enum GROUP_ID {
		GROUP0;

		public boolean isEvent(RecvEvent re) {
			return ordinal() == re.getEventID();
		}
	};

	enum INPUT_ID {
		INPUT0,
	};

	public FsxPanel() throws Exception {
		int protoVersion = 0x2;

		final SimConnect sc = new SimConnect("Fsx Panel", 0);

		// altitude
		final String variable = "AIRSPEED INDICATED";
		int cid = 0;
		SimConnectPeriod p = SimConnectPeriod.SECOND;
		String units = "knots";
		sc.addToDataDefinition(1, variable, units, SimConnectDataType.INT32);
		sc.requestDataOnSimObject(1, 1, cid, p);

		sc.mapClientEventToSimEvent(EVENT_ID.KEY_TOGGLE_AVIONICS_MASTER, "TOGGLE_AVIONICS_MASTER");
		sc.addClientEventToNotificationGroup(GROUP_ID.GROUP0, EVENT_ID.KEY_TOGGLE_AVIONICS_MASTER);
		sc.setNotificationGroupPriority(GROUP_ID.GROUP0, NotificationPriority.HIGHEST);

		// Note that this does not override "." for brakes - both with be
		// transmitted

		// sc.mapInputEventToClientEvent(INPUT_ID.INPUT0, "shift+ctrl+u",
		// EVENT_ID.KEY_PANEL_LIGHTS_TOGGLE);
		//
		// sc.setInputGroupState(INPUT_ID.INPUT0, true);

		// sc.transmitClientEvent(SimConnectConstants.OBJECT_ID_USER,
		// EVENT_ID.KEY_TOGGLE_AVIONICS_MASTER, 0, GROUP_ID.GROUP0,
		// SimConnectConstants.EVENT_FLAG_DEFAULT);

		DispatcherTask dt = new DispatcherTask(sc);
		dt.addOpenHandler(new OpenHandler() {
			public void handleOpen(SimConnect sender, RecvOpen e) {
				System.out.println("Connected to " + e.getApplicationName());
			}
		});
		// dt.addEventHandler(this);
		dt.addExceptionHandler(new ExceptionHandler() {
			public void handleException(SimConnect sender, RecvException e) {
				System.out.println("Exception (" + e.getException() + ") packet " + e.getSendID());
			}
		});
		dt.addSimObjectDataHandler(new SimObjectDataHandler() {
			public void handleSimObject(SimConnect sender, RecvSimObjectData e) {
				newAirspeed = e.getDataInt32();
				if (newAirspeed != oldAirspeed) {
					oldAirspeed = newAirspeed;
					System.out.println("Current " + variable + ": " + newAirspeed);
				}
			}
		});
		dt.createThread().start();

		GPIOManager pi = new GPIOManager();
		// app.setup();

		while (true) {
			try {
				if (pi.buttonPressed()) {
					sc.transmitClientEvent(SimConnectConstants.OBJECT_ID_USER, EVENT_ID.KEY_TOGGLE_AVIONICS_MASTER, 0,
							GROUP_ID.GROUP0, SimConnectConstants.EVENT_FLAG_DEFAULT);
					Thread.sleep(500);
				}
				if (newAirspeed > 0 && newAirspeed <= 40) {
					pi.red();
				} else if (newAirspeed > 100) {
					pi.yellow();
				} else if (newAirspeed > 40) {
					pi.green();
				} else {
					pi.ledOff();
				}
			} catch (InterruptedException ie) {
				pi.kill();
				System.out.println("Shutting down GPIO...");
			}
		}

	}

	// @Override
	public void handleOpen(SimConnect sender, RecvOpen e) {
		System.out.println("Connected to : " + e.getApplicationName() + " " + e.getApplicationVersionMajor() + "."
				+ e.getApplicationVersionMinor());
	}

	// @Override
	// public void handleEvent(SimConnect sender, RecvEvent e) {
	// if (e.getEventID() == EVENT_ID.KEY_PANEL_LIGHTS_TOGGLE.ordinal()) {
	// System.out.println("Event lights: " + e.getData());
	// }
	//
	// }
}
