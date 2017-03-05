package drbookings;

import java.util.Collection;

import com.gargoylesoftware.htmlunit.util.Cookie;

public class SessionBooking {

    private Collection<Cookie> cookies;

    private String sessionId;

    private String currentUrl;

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof SessionBooking)) {
	    return false;
	}
	final SessionBooking other = (SessionBooking) obj;
	if (cookies == null) {
	    if (other.cookies != null) {
		return false;
	    }
	} else if (!cookies.equals(other.cookies)) {
	    return false;
	}
	if (currentUrl == null) {
	    if (other.currentUrl != null) {
		return false;
	    }
	} else if (!currentUrl.equals(other.currentUrl)) {
	    return false;
	}
	if (sessionId == null) {
	    if (other.sessionId != null) {
		return false;
	    }
	} else if (!sessionId.equals(other.sessionId)) {
	    return false;
	}
	return true;
    }

    public Collection<Cookie> getCookies() {
	return cookies;
    }

    public String getCurrentUrl() {
	return currentUrl;
    }

    public String getSessionId() {
	return sessionId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((cookies == null) ? 0 : cookies.hashCode());
	result = prime * result + ((currentUrl == null) ? 0 : currentUrl.hashCode());
	result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
	return result;
    }

    public boolean isEmpty() {
	return sessionId == null || cookies == null;
    }

    public boolean isValid() {
	return !isEmpty();
    }

    public void setCookies(final Collection<Cookie> cookies) {
	this.cookies = cookies;
    }

    public void setCurrentUrl(final String currentUrl) {
	this.currentUrl = currentUrl;
    }

    public void setSessionId(final String sessionId) {
	this.sessionId = sessionId;
    }

    @Override
    public String toString() {
	return "SessionBooking [cookies=" + cookies + ", sessionId=" + sessionId + ", currentUrl=" + currentUrl + "]";
    }

}
