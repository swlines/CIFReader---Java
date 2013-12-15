package uk.co.swlines.cifreader.exceptions;

import uk.co.swlines.cifreader.cif.data.CIFSchedule;

public class LogicException extends Exception {
	protected CIFSchedule schedule;

	public LogicException(CIFSchedule schedule) {
		this.schedule = schedule;
	}
	
	@Override
	public String getMessage() {
		return new StringBuilder(schedule.getUid()).append(" running from ").append(schedule.getDate_from()).append(" (STP ").append(schedule.getStp_indicator()).append(")").toString();
	}

}
