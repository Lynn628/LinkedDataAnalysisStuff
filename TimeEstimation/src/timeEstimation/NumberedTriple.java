package timeEstimation;


public class NumberedTriple {

	private Long subjectNo;
	private Long objectNo;
	
	public NumberedTriple(Long subjectNo, Long objectNo) {
		this.subjectNo = subjectNo;
		this.objectNo = objectNo;
	}
	public Long getObjectNo() {
		return objectNo;
	}
	public void setObjectNo(Long objectNo) {
		this.objectNo = objectNo;
	}
	
	public Long getSubjectNo() {
		return subjectNo;
	}
	
	public void setSubjectNo(Long subjectNo) {
		this.subjectNo = subjectNo;
	}
	
	@Override
	public String toString() {
		return  subjectNo.toString() + "  " + objectNo.toString();
	}

}
