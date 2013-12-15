package uk.co.swlines.cifreader.cif.data;

public interface CIFLocationDepart {
	public Integer getDeparture();
	public Integer getPublic_departure();
	public String getLine();
	
	public int getAllowance_engineering();
	public int getAllowance_pathing();
	public int getAllowance_performance();
}
