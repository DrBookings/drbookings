package drbookings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DrBookingsApplication extends Application {

    private final static Logger logger = LoggerFactory.getLogger(DrBookingsApplication.class);

    public final static int DEFAULT_WAIT_FOR_BACKGROUND_JAVA_SCRIPT_WAIT_TIME = 1000;

    private final static String regExSession = "ses=([a-z0-9]+)(\\&)*?";
    private final static Pattern patternSession = Pattern.compile(regExSession);

    private static boolean askForVerification(final HtmlPage p) {
	return p.asText().contains("Please choose a verification method");
    }

    public static HtmlPage clickAndWait(final HtmlForm input) throws IOException {
	final HtmlPage p = input.click();
	int i = 1;
	while (i != 0) {
	    i = p.getWebClient().waitForBackgroundJavaScript(DEFAULT_WAIT_FOR_BACKGROUND_JAVA_SCRIPT_WAIT_TIME);
	}
	return p;
    }

    public static HtmlPage clickAndWait(final HtmlInput input) throws IOException {
	final HtmlPage p = input.click();
	int i = 1;
	while (i != 0) {
	    i = p.getWebClient().waitForBackgroundJavaScript(DEFAULT_WAIT_FOR_BACKGROUND_JAVA_SCRIPT_WAIT_TIME);
	}
	return p;
    }

    private static String getSessionId(final URL baseURL) {

	final Matcher matcher = patternSession.matcher(baseURL.toString());
	while (matcher.find()) {
	    return matcher.group(1);
	}
	throw new RuntimeException("Failed to get session id from " + baseURL);
    }

    private static boolean loggedIn(final HtmlPage page) {
	return !page.asText().contains("session has expired");
    }

    private static HtmlPage logIn(final WebClient client) {
	try {
	    HtmlPage page = client.getPage("https://admin.booking.com/");
	    final HtmlInput inputPassword = page.getFirstByXPath("//input[@type='password']");
	    // The first preceding input that is not hidden
	    final HtmlInput inputLogin = inputPassword.getFirstByXPath(".//preceding::input[not(@type='hidden')]");
	    inputLogin.setValueAttribute("1465954");
	    inputPassword.setValueAttribute("pinkiwinki22");
	    // get the enclosing form
	    final HtmlForm loginForm = inputPassword.getEnclosingForm();
	    // submit the form
	    final WebRequest webrequest = loginForm.getWebRequest(null);
	    page = client.getPage(webrequest);
	    final String sessionId = getSessionId(page.getBaseURL());
	    System.out.println("Login result: " + page.getBaseURL());
	    System.out.println("SessionId: " + sessionId);
	    if (loggedIn(page)) {
		final SessionBooking session = new SessionBooking();
		session.setSessionId(getSessionId(page.getBaseURL()));
		session.setCookies(client.getCookieManager().getCookies());
		session.setCurrentUrl(page.getUrl().toString());
		writeSession(session);
		return page;
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    private static void los(HtmlPage page) {
	final int maxLogInCount = 2;
	int logInCount = 0;
	while (!loggedIn(page) && logInCount < maxLogInCount) {
	    System.out.println("Logging in.. (" + logInCount + ")");
	    page = logIn(page.getWebClient());
	    if (loggedIn(page)) {
		break;
	    }
	    logInCount++;
	}
	System.out.println(page.getBaseURL());
	// System.out.println(page.asText());
	// System.out.println(page.asXml());

	final List<HtmlAnchor> bookings = new ArrayList<>();

	for (final HtmlAnchor anchor : page.getAnchors()) {
	    final String str = anchor.asText();
	    if (anchor.getAttribute("data-type").equals("booking")) {
		bookings.add(anchor);
	    }
	}
	System.out.println("Found bookings:");
	for (final HtmlAnchor a : bookings) {
	    System.out.println(a.asText());
	}

	System.out.println("Collecting details");
	for (final HtmlAnchor a : bookings) {

	    try {
		HtmlPage p = a.click();
		while (askForVerification(p)) {
		    p = verify(p);
		}

		final int j = 0;
		System.out.println(p.asXml());
		final int k = 0;

	    } catch (final IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	}

	final int i = 0;

    }

    private static void loslos() {
	try {
	    HtmlPage page = null;
	    final WebClient client = new WebClient(BrowserVersion.CHROME);
	    client.setAjaxController(new AjaxController() {
		@Override
		public boolean processSynchron(final HtmlPage page, final WebRequest request, final boolean async) {
		    return true;
		}
	    });
	    // client.getOptions().setCssEnabled(false);
	    // client.getOptions().setJavaScriptEnabled(false);
	    final SessionBooking session = readSession();
	    page = client.getPage(
		    "https://admin.booking.com/hotel/hoteladmin/extranet_ng/manage/upcoming_reservations.html?ses="
			    + session.getSessionId() + "&lang=en&hotel_id=1465954");

	    los(page);

	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    public static void main(final String[] args) {
	launch(args);

    }

    @SuppressWarnings("unchecked")
    private static SessionBooking readSession() {
	InputStream fis = null;
	final SessionBooking session = new SessionBooking();
	try {
	    fis = new FileInputStream("drbookings-storage");
	    final ObjectInputStream o = new ObjectInputStream(fis);
	    final String url = (String) o.readObject();
	    final String sessionId = (String) o.readObject();
	    final Collection<Cookie> cookies = (Collection<Cookie>) o.readObject();
	    session.setCurrentUrl(url);
	    session.setSessionId(sessionId);
	    session.setCookies(cookies);
	} catch (final Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		fis.close();
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	}
	return session;
    }

    private static HtmlPage verify(HtmlPage p) throws IOException {
	try {
	    if (p.asText().contains("Verify now")) {
		final HtmlInput input3 = p.getElementByName("ask_pin");
		final TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Enter PIN");
		dialog.setHeaderText("Enter PIN");
		dialog.setContentText("Enter PIN");
		final Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
		    p = (HtmlPage) input3.setValueAttribute(result.get());
		    // System.out.println(input3.asXml());
		    final HtmlForm form = p.getFirstByXPath("//form[@id='enter_security_pin']");
		    final HtmlInput input4 = p.getFirstByXPath("//input[@value='Verify now']");
		    // System.out.println(input4.getParentNode().asXml());
		    final HtmlInput input5 = p.getFirstByXPath("//input[@name='check_pin_auth']");
		    // p = (HtmlPage) input5.setValueAttribute(result.get());
		    // System.out.println(input5.getParentNode().asXml());
		    // p = clickAndWait(input5);
		    // p = clickAndWait(input4);
		    // System.out.println(p.asXml());
		    p = clickAndWait(input4);
		    p = clickAndWait(form);
		    if (p.asText().contains("You've successfully verified and saved this new device or location.")) {
			logger.info("Successfully verified");
		    }
		    return p;
		}
	    } else {
		final HtmlInput input1 = p.getForms().get(0).getInputByValue("sms");
		p = clickAndWait(input1);
		final HtmlInput input2 = p.getForms().get(0).getInputByValue("Send SMS");
		p = clickAndWait(input2);
	    }

	} catch (final Exception e) {
	    e.printStackTrace();
	    // System.out.println(p.asXml());
	    verify((HtmlPage) p.refresh());
	}
	return p;
    }

    private static void writeSession(final SessionBooking session) {
	OutputStream fos = null;
	try {
	    fos = new FileOutputStream("drbookings-storage");
	    final ObjectOutputStream o = new ObjectOutputStream(fos);
	    o.writeObject(session.getCurrentUrl());
	    o.writeObject(session.getSessionId());
	    o.writeObject(session.getCookies());
	} catch (final Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		fos.close();
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
	primaryStage.setTitle("Dr.Bookings");
	final StackPane root = new StackPane();
	primaryStage.setScene(new Scene(root, 300, 250));
	primaryStage.show();
	Platform.runLater(() -> loslos());
    }

}
