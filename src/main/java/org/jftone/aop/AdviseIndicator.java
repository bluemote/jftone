package org.jftone.aop;

public class AdviseIndicator {
	private boolean hasBefore = false;
	private boolean hasBeforeArg = false;
	
	private boolean hasAfter = false;
	private boolean hasAfterArg = false;
	
	private boolean hasAround = false;
	
	private boolean hasAfterReturning = false;
	private boolean hasAfterReturningArg = false;
	
	private boolean hasThrowing = false;
	private boolean hasThrowingArg = false;
	public boolean hasBefore() {
		return hasBefore;
	}
	public void setHasBefore(boolean hasBefore) {
		this.hasBefore = hasBefore;
	}
	public boolean hasBeforeArg() {
		return hasBeforeArg;
	}
	public void setHasBeforeArg(boolean hasBeforeArg) {
		this.hasBeforeArg = hasBeforeArg;
	}
	public boolean hasAfter() {
		return hasAfter;
	}
	public void setHasAfter(boolean hasAfter) {
		this.hasAfter = hasAfter;
	}
	public boolean hasAfterArg() {
		return hasAfterArg;
	}
	public void setHasAfterArg(boolean hasAfterArg) {
		this.hasAfterArg = hasAfterArg;
	}
	public boolean hasAround() {
		return hasAround;
	}
	public void setHasAround(boolean hasAround) {
		this.hasAround = hasAround;
	}
	public boolean hasAfterReturning() {
		return hasAfterReturning;
	}
	public void setHasAfterReturning(boolean hasAfterReturning) {
		this.hasAfterReturning = hasAfterReturning;
	}
	public boolean hasAfterReturningArg() {
		return hasAfterReturningArg;
	}
	public void setHasAfterReturningArg(boolean hasAfterReturningArg) {
		this.hasAfterReturningArg = hasAfterReturningArg;
	}
	public boolean hasThrowing() {
		return hasThrowing;
	}
	public void setHasThrowing(boolean hasThrowing) {
		this.hasThrowing = hasThrowing;
	}
	public boolean hasThrowingArg() {
		return hasThrowingArg;
	}
	public void setHasThrowingArg(boolean hasThrowingArg) {
		this.hasThrowingArg = hasThrowingArg;
	}
	
}
