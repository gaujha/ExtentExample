package com.vsysq.base;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class TestListener implements ITestListener {

	public static ExtentTest test;
	public static ExtentTest childTest;
	public static WebDriver driver = null;

	static String timeStamp = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
	static String timeStamp1 = new SimpleDateFormat("HH-mm-ss").format(new Date());
	static Path path = Paths.get("/Test Reports/" + timeStamp +"/"+ timeStamp1);
	
	public void onStart(ITestContext context) {
		System.out.println("*** Test Suite " + context.getName() + " started ***");
	}

	public void onFinish(ITestContext context) {
		System.out.println(("*** Test Suite " + context.getName() + " ending ***"));
		ExtentTestManager.endTest();
		ExtentManager.getInstance().flush();
	}

	public void onTestStart(ITestResult result) {
		System.out.println(("*** Running test method " + result.getMethod().getMethodName() + "..."));
		ExtentTestManager.startTest(result.getMethod().getMethodName());
	}

	public void onTestSuccess(ITestResult result) {
		System.out.println("*** Executed " + result.getMethod().getMethodName() + " test successfully...");
		ExtentTestManager.getTest().log(Status.PASS, "Test passed");
	}

	public void onTestFailure(ITestResult result) {
		System.out.println("*** Test execution " + result.getMethod().getMethodName() + " failed...");
		ExtentTestManager.getTest().log(Status.FAIL, "Test Failed");
	}

	public void onTestSkipped(ITestResult result) {
		System.out.println("*** Test " + result.getMethod().getMethodName() + " skipped...");
		ExtentTestManager.getTest().log(Status.SKIP, "Test Skipped");
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		System.out.println("*** Test failed but within percentage % " + result.getMethod().getMethodName());
	}
	
	public void failed(ITestResult result) throws IOException 
	{
		childTest.log(Status.FAIL, "TEST CASE FAILED " + result.getName());
		childTest.log(Status.FAIL, "TEST CASE FAILED " + result.getThrowable());
		
		String screenshotPath = TestListener.getScreenshot(driver, result.getName());
		childTest.fail("testcase failed", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
	}

	public void success(ITestResult result) throws IOException 
	{
		childTest.log(Status.PASS, "Test Case PASSED " + result.getName());
		String screenshotPath = TestListener.getScreenshot(driver, result.getName());
		childTest.pass("testcase passed", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
	}

	public void skipped(ITestResult result) throws IOException 
	{
		childTest.log(Status.SKIP, "Test Case SKIPPED " + result.getName());
	}

	public static String getScreenshot(WebDriver driver, String screenshotName) throws IOException
	{
		String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		Screenshot screenshot=new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
		String destination = System.getProperty("user.dir") + path + "/" + screenshotName + dateName + ".png";
		ImageIO.write(screenshot.getImage(),"PNG",new File(destination));
		return destination;
	}
	
	public static void logStep(String stepDescription)
	{
		childTest = test.createNode(stepDescription);
	}

	public static ExtentTest assignAuthor(String author)
	{
		return test.assignAuthor(author);
	}

}
