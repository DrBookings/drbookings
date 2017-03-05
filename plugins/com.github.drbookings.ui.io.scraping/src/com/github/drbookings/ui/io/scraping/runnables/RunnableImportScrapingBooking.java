package com.github.drbookings.ui.io.scraping.runnables;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.core.api.BookingManager;
import com.github.drbookings.core.datamodel.api.Booking;
import com.jaunt.Document;
import com.jaunt.UserAgent;

import net.sf.kerner.utils.rcp.RunnableProto;

public class RunnableImportScrapingBooking extends RunnableProto<List<Booking>> {

    private final static Logger logger = LoggerFactory.getLogger(RunnableImportScrapingBooking.class);

    private final String loginURL = "https://admin.booking.com/hotel/hoteladmin/extranet_ng/manage/new_and_updated_reservations.html?lang=en&hotel_id=1465954&ses=0";

    private final BookingManager manager;

    public RunnableImportScrapingBooking(final BookingManager manager) {
	this.manager = manager;

    }

    @Override
    protected void onSuccess(final List<Booking> result) {

	super.onSuccess(result);
	manager.addAllBookings(result);

    }

    @Override
    protected List<Booking> process(final IProgressMonitor monitor) throws Exception {

	try {

	    // final Document document = Jsoup.connect(loginURL)
	    // .data("loginname", "1465954", "password", "pinkiwinki21",
	    // "submit", "Login").post();
	    //
	    // System.err.println(document);
	    System.out.println();
	    System.out.println("------------------------------------------");
	    final UserAgent userAgent = new UserAgent();
	    final Document doc = userAgent.visit("https://admin.booking.com/");
	    System.out.println(userAgent.getLocation());
	    doc.fillout("Login name", "1465954");
	    System.out.println(userAgent.getLocation());
	    doc.fillout("Password", "pinkiwinki22");
	    System.out.println(userAgent.getLocation());
	    doc.choose("Language", "English");
	    System.out.println(userAgent.getLocation());
	    doc.submit("Log in");
	    System.out.println(userAgent.getLocation());

	    // final WebClient webClient = new WebClient(BrowserVersion.CHROME);
	    // webClient.getOptions().setRedirectEnabled(true);
	    // webClient.getOptions().setCssEnabled(false);
	    // webClient.getOptions().setThrowExceptionOnScriptError(false);
	    // webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	    // webClient.getOptions().setUseInsecureSSL(true);
	    // webClient.getOptions().setJavaScriptEnabled(true);
	    // webClient.getCookieManager().setCookiesEnabled(true);
	    //
	    // final HtmlPage page1 =
	    // webClient.getPage("https://admin.booking.com/");
	    //
	    // // Wait for background Javascript
	    // webClient.waitForBackgroundJavaScript(2000);
	    //
	    // System.out.println(page1.getForms());
	    //
	    // // Get first form on page
	    // final HtmlForm form = page1.getForms().get(1);
	    //
	    // // Get login input fields using input field name
	    // final HtmlTextInput userName = form.getInputByName("loginname");
	    // final HtmlPasswordInput password =
	    // form.getInputByName("password");
	    //
	    // // Set input values
	    // userName.setValueAttribute("1465954");
	    // password.setValueAttribute("pinkiwinki22");
	    //
	    // // Find the first button in form using name, id or xpath
	    // final HtmlElement button = (HtmlElement)
	    // form.getFirstByXPath("//button");
	    // //
	    // // // Post by clicking the button and cast the result, login
	    // arrival
	    // // // url, to a new page and repeat what you did with page1 or
	    // // // something else :)
	    // final HtmlPage page2 = (HtmlPage) button.click();
	    // //
	    // // // Profit
	    // System.out.println(page2.asText());

	    return Collections.emptyList();
	} finally {
	    monitor.done();
	}

    }

}
