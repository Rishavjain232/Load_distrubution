package org.app.iiitb.peorig;

public class DataCenterDetails {
	private String id;
	private String broker;
	private double capacity[];
	private double greenEnergy;

	public DataCenterDetails() {
		capacity = new double[60];
	}

	@Override
	public String toString() {
		return "DataCenterDetails:: [id=" + id + ", broker=" + broker + ", capacity=" + capacity + ", greenEnergy="
				+ greenEnergy + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double[] getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity[]) {
		this.capacity = capacity;
	}

	public double getGreenEnergy() {
		return greenEnergy;
	}

	public void setGreenEnergy(double greenEnergy) {
		this.greenEnergy = greenEnergy;
	}

	public String getBroker() {
		return broker;
	}
	
	public void setBroker(String broker) {
		this.broker = broker;
	}
	
	
}
