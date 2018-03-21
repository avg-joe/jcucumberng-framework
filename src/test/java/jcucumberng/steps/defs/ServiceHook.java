package jcucumberng.steps.defs;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import jcucumberng.api.Configuration;
import jcucumberng.api.LocalSystem;

public class ServiceHook {
	private enum Browser {
		CHROME, CHROME_NOHEAD, FF32, FF32_NOHEAD, FF64, FF64_NOHEAD, EDGE, IE32, IE64
	}

	private static final Logger logger = LogManager.getLogger(ServiceHook.class);
	private WebDriver driver = null;

	@Before
	public void setUp() throws Throwable {
		StringBuilder builder = new StringBuilder();
		builder.append(System.getProperty("user.dir").replace("\\", "/"));
		builder.append("/src/test/resources/webdrivers/");
		String driverPath = builder.toString().trim();

		FirefoxBinary ffBin = null;
		FirefoxOptions ffOpts = null;

		logger.info("Initializing webdriver...");
		String browserConfig = Configuration.readKey("browser");
		logger.info("Browser: " + browserConfig);

		Browser browser = null;
		try {
			if (!StringUtils.isBlank(browserConfig)) {
				browser = Browser.valueOf(browserConfig.toUpperCase());
			} else {
				logger.error("Unspecified browser in config. Using default " + Browser.CHROME_NOHEAD + ".");
				browser = Browser.CHROME_NOHEAD;
			}
		} catch (IllegalArgumentException iae) {
			logger.error("Unsupported browser specified in config. Using default " + Browser.CHROME_NOHEAD + ".");
			browser = Browser.CHROME_NOHEAD;
		}
		if (null == browser) {
			logger.error("Browser is null. Using default " + Browser.CHROME_NOHEAD + ".");
			browser = Browser.CHROME_NOHEAD;
		}

		switch (browser) {
		case CHROME:
			System.setProperty("webdriver.chrome.driver", driverPath + "chromedriver_win32.exe");
			driver = new ChromeDriver();
			break;
		case CHROME_NOHEAD:
			System.setProperty("webdriver.chrome.driver", driverPath + "chromedriver_win32.exe");
			ChromeOptions chromeOpts = new ChromeOptions();
			chromeOpts.addArguments("--headless");
			chromeOpts.addArguments("--window-size=" + LocalSystem.getNativeResolution());
			driver = new ChromeDriver(chromeOpts);
			break;
		case FF32:
			System.setProperty("webdriver.gecko.driver", driverPath + "geckodriver_win32.exe");
			driver = new FirefoxDriver();
			break;
		case FF32_NOHEAD:
			System.setProperty("webdriver.gecko.driver", driverPath + "geckodriver_win32.exe");
			ffBin = new FirefoxBinary();
			ffBin.addCommandLineOptions("--headless");
			ffOpts = new FirefoxOptions();
			ffOpts.setBinary(ffBin);
			ffOpts.setLogLevel(FirefoxDriverLogLevel.INFO);
			driver = new FirefoxDriver(ffOpts);
			break;
		case FF64:
			System.setProperty("webdriver.gecko.driver", driverPath + "geckodriver_win64.exe");
			driver = new FirefoxDriver();
			break;
		case FF64_NOHEAD:
			System.setProperty("webdriver.gecko.driver", driverPath + "geckodriver_win64.exe");
			ffBin = new FirefoxBinary();
			ffBin.addCommandLineOptions("--headless");
			ffOpts = new FirefoxOptions();
			ffOpts.setBinary(ffBin);
			ffOpts.setLogLevel(FirefoxDriverLogLevel.INFO);
			driver = new FirefoxDriver(ffOpts);
			break;
		case EDGE:
			System.setProperty("webdriver.edge.driver", driverPath + "MicrosoftWebDriver.exe");
			driver = new EdgeDriver();
			break;
		case IE32:
			System.setProperty("webdriver.ie.driver", driverPath + "IEDriverServer_win32.exe");
			driver = new InternetExplorerDriver();
			break;
		case IE64:
			System.setProperty("webdriver.ie.driver", driverPath + "IEDriverServer_win64.exe");
			driver = new InternetExplorerDriver();
			break;
		default:
			// Will not execute because of error handling before the switch
			break;
		}
	}

	@After
	public void tearDown() throws Throwable {
		logger.info("Terminating webdriver...");
		driver.quit();
	}

	public WebDriver getDriver() {
		return driver;
	}

}
