package net.sf.openrocket.motor;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.sf.openrocket.util.TextUtil;

public class MotorDigest {
	
	private static final double EPSILON = 0.00000000001;
	
	public enum DataType {
		/** An array of time points at which data is available (in ms) */
		TIME_ARRAY(0, 1000),
		/** Mass data for a few specific points (normally initial and empty mass) (in 0.1g) */
		MASS_SPECIFIC(1, 10000),
		/** Mass per time (in 0.1g) */
		MASS_PER_TIME(2, 10000),
		/** CG position for a few specific points (normally initial and final CG) (in mm) */
		CG_SPECIFIC(3, 1000),
		/** CG position per time (in mm) */
		CG_PER_TIME(4, 1000),
		/** Thrust force per time (in mN) */
		FORCE_PER_TIME(5, 1000);

		private final int order;
		private final int multiplier;
		DataType(int order, int multiplier) {
			this.order = order;
			this.multiplier = multiplier;
		}
		public int getOrder() {
			return order;
		}
		public int getMultiplier() {
			return multiplier;
		}
	}
	

	private final MessageDigest digest;
	private boolean used = false;
	private int lastOrder = -1;
	
	
	public MotorDigest() {
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 digest not supported by JRE", e);
		}
	}
	
	
	public void update(DataType type, int ... values) {

		// Check for correct order
		if (lastOrder >= type.getOrder()) {
			throw new IllegalArgumentException("Called with type="+type+" order="+type.getOrder()+
					" while lastOrder=" + lastOrder);
		}
		lastOrder = type.getOrder();
		
		// Digest the type
		digest.update(bytes(type.getOrder()));
		
		// Digest the data length
		digest.update(bytes(values.length));
		
		// Digest the values
		for (int v: values) {
			digest.update(bytes(v));
		}
		
	}
	
	
	private void update(DataType type, int multiplier, double ... values) {

		int[] intValues = new int[values.length];
		for (int i=0; i<values.length; i++) {
			double v = values[i];
			v = next(v);
			v *= multiplier;
			v = next(v);
			intValues[i] = (int) Math.round(v);
		}
		update(type, intValues);
	}
	
	public void update(DataType type, double ... values) {
		update(type, type.getMultiplier(), values);
	}
	
	private static double next(double v) {
		return v + Math.signum(v) * EPSILON;
	}
	
	
	public String getDigest() {
		if (used) {
			throw new IllegalStateException("MotorDigest already used");
		}
		used = true;
		byte[] result = digest.digest();
		return TextUtil.hexString(result);
	}

	
	
	private byte[] bytes(int value) {
		return new byte[] {
				(byte) ((value>>>24) & 0xFF), (byte) ((value>>>16) & 0xFF),
				(byte) ((value>>>8) & 0xFF), (byte) (value & 0xFF) 
		};
	}

	
	
	
	public static String digestComment(String comment) {
		comment = comment.replaceAll("\\s+", " ").trim();
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 digest not supported by JRE", e);
		}
		
		try {
			digest.update(comment.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 encoding not supported by JRE", e);
		}

		return TextUtil.hexString(digest.digest());
	}
	
}
