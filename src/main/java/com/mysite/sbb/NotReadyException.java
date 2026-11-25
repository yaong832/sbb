package com.mysite.sbb;

/**
 * 학습 데이터나 학습된 모델이 아직 없어 요청을 처리할 수 없을 때 사용.
 * GlobalExceptionHandler가 500 오류 대신 안내 화면(notice.html)을 보여 준다.
 */
public class NotReadyException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String nextUrl;
	private final String nextLabel;

	public NotReadyException(String message, String nextUrl, String nextLabel) {
		super(message);
		this.nextUrl = nextUrl;
		this.nextLabel = nextLabel;
	}

	public String getNextUrl() {
		return nextUrl;
	}

	public String getNextLabel() {
		return nextLabel;
	}
}
