package net.sf.openrocket.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import net.sf.openrocket.file.GeneralMotorLoader;
import net.sf.openrocket.file.MotorLoader;
import net.sf.openrocket.motor.Motor;
import net.sf.openrocket.motor.ThrustCurveMotor;

public class MotorPrinter {

	public static void main(String[] args) throws IOException {

		MotorLoader loader = new GeneralMotorLoader();
		
		System.out.println();
		for (String arg: args) {
			InputStream stream = new FileInputStream(arg);
			
			List<Motor> motors = loader.load(stream, arg);
			
			System.out.println("*** " + arg + " ***");
			System.out.println();
			for (Motor m: motors) {
				System.out.println("  Manufacturer:  " + m.getManufacturer());
				System.out.println("  Designation:   " + m.getDesignation());
				System.out.println("  Delays:        " + 
						Arrays.toString(m.getStandardDelays()));
				System.out.printf("  Nominal time:  %.2f s\n",  m.getAverageTime());
				System.out.printf("  Total time:    %.2f s\n",  m.getTotalTime());
				System.out.printf("  Avg. thrust:   %.2f N\n",  m.getAverageThrust());
				System.out.printf("  Max. thrust:   %.2f N\n",  m.getMaxThrust());
				System.out.printf("  Total impulse: %.2f Ns\n", m.getTotalImpulse());
				System.out.println("  Diameter:      " + m.getDiameter()*1000 + " mm");
				System.out.println("  Length:        " + m.getLength()*1000 + " mm");
				System.out.println("  Digest:        " + m.getDigestString());
				
				if (m instanceof ThrustCurveMotor) {
					ThrustCurveMotor tc = (ThrustCurveMotor)m;
					System.out.println("  Data points:   " + tc.getTimePoints().length);
				}
				
				System.out.println("  Comment:");
				System.out.println(m.getDescription());
				System.out.println();
			}
		}
	}
}

