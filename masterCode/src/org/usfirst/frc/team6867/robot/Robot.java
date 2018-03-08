/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6867.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {

	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	private int stationNumber;

	// Declare the drive motors. They're all on Victor motor controllers
	Victor frontLeftDrive=new Victor (1);	
	Victor backLeftDrive=new Victor (0);
	Victor frontRightDrive=new Victor (3);
	Victor backRightDrive=new Victor (2);

	// Delcare the intake motors. They're on the TalonSRX controllers
	WPI_TalonSRX frontLeftIntake = new WPI_TalonSRX(10);
	WPI_TalonSRX frontRightIntake = new WPI_TalonSRX(20);
	WPI_TalonSRX backRightIntake = new WPI_TalonSRX(21);
	WPI_TalonSRX backLeftIntake = new WPI_TalonSRX(11);	
	
	// Declare the controller. We're using the Logitech gamepad
	Joystick gamepad=new Joystick (0);

	boolean autoEnabled = true; // This flag will let us prevent the periodic auto from looping
	double autoDelay = 0; // This allows us to delay the start of our autonomous, in case our alliance needs us to wait.
	boolean driverSelect = false; // We can use this to toggle between Ishmam and Clark's driver code
	
	double speedMaster = 0.7; // Used in Ishmam's GTA code
	
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		CameraServer.getInstance().startAutomaticCapture();
	}
	
	//In early builds of our code the rightDrive and leftDrive functions were actually backwards.
	//This has been corrected in this build, and the auto has been updated to reflect that.
	
	public void rightDrive(double speed) {
		frontRightDrive.set(-speed);
		backRightDrive.set(-speed);
	}
	
	public void leftDrive(double speed) {
		frontLeftDrive.set(speed);
		backLeftDrive.set(speed);
	}
	
	// A handy function to stop drive functions. Includes a 50ms delay to help kill momentum before moving on.
	
	public void halt() {
		frontLeftDrive.set(0);
		backLeftDrive.set(0);
		frontRightDrive.set(0);
		backRightDrive.set(0);
		wait1MSec(50);
	}

	
	// wait1MSec exists only to mimic a function that's familiar to anyone with Vex/RobotC experience.
	// This will pause execution of code for a set duration (in milliseconds), allowing for simple drive-for-time behaviours
	
	public void wait1MSec(long time){
		long Time0 = System.currentTimeMillis();
	    long Time1;
	    long runTime = 0;
	    while(runTime<time){
	        Time1 = System.currentTimeMillis();
	        runTime = Time1 - Time0;
	    }
	}
	

	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
	}

	@Override
	public void disabledPeriodic() {
		// Use gamepad to select which autonomous to run
		if (gamepad.getRawButton(3)) { //LEFT
			stationNumber = 1;
			SmartDashboard.putString("DB/String 0", "Auto: Left");
		}
		else if (gamepad.getRawButton(1)) {
			stationNumber = 2;
			SmartDashboard.putString("DB/String 0", "Auto: Center");
		}
		
		else if (gamepad.getRawButton(2)) {
			stationNumber = 3;
			SmartDashboard.putString("DB/String 0", "Auto: Right");
		}
		
		// This will read a value off of the first slider, and use it to delay the start of autonomous up to 5s.
		autoDelay = SmartDashboard.getNumber("DB/Slider 0", 0.0) * 1000; // It can read a value from 0 to 5, but we need a value in millisecs
		SmartDashboard.putString("DB/String 1", "autoDelay: " + Double.toString(autoDelay)); //JT: Not sure if this is legal...
		
		// And this will use the top-most "button" to toggle driver mode. Defaults to Clark, toggle on for Ishmam
		driverSelect = SmartDashboard.getBoolean("DB/Button 0", false);
		if (driverSelect == false) {
			SmartDashboard.putString("DB/String 2", "Driver: Clark (Tank)");
		}
		else {
			SmartDashboard.putString("DB/String 2", "Driver: Ishmam (GTA)");
		}
		
	}	
	
	
	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//int stationNumber = DriverStation.getInstance().getLocation();  //I actually don't know if the stations are 0/1/2 or 1/2/3 
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();

		
		
		if(gameData.length() > 0 && autoEnabled == true) { // Call red 0? Sake of argument?
			if(stationNumber == 1) {
				wait1MSec((long)autoDelay);
				leftDrive(-.75);//forward
				rightDrive(-.72);
				wait1MSec(2000);
		        leftDrive(0.2);//counter brake
		        rightDrive(0.2);
		        wait1MSec(100);
		        halt();
		        autoEnabled = false;
		    
			}
			
			else if(stationNumber == 2) {
				wait1MSec((long)autoDelay);
		        leftDrive(-.7);//straight 1
		        rightDrive(-.7);
		        wait1MSec(400);
		        leftDrive(0.2);//counter brake
		        rightDrive(0.2);
		        wait1MSec(50);
		        halt();
		        
				if(gameData.charAt(0) == 'R') {// to the right side of switch
    
			        leftDrive(.45);//turn 1
			        rightDrive(-.7);
			        wait1MSec(400);
			        leftDrive(0.2);//counter brake
			        rightDrive(0.2);
			        wait1MSec(50);
			        halt();
			        
			        leftDrive(-.7);//straight 2
			        rightDrive(-.7);
			        wait1MSec(570);
			        leftDrive(0.2);//counter brake
			        rightDrive(0.2);
			        wait1MSec(50);
			        halt();
			        
			        leftDrive(-.35);//turn 2
			        rightDrive(.7);
			        wait1MSec(695);
			        leftDrive(0.2);//counter brake
			        rightDrive(0.2);
			        wait1MSec(50);
			        halt();
			        
			        		 			
				} 
				else if(gameData.charAt(0) == 'L'){ //left side of the switch
			     
			        leftDrive(-.45);//turn 1
			        rightDrive(.7);
			        wait1MSec(550);
			        leftDrive(0.2);//counter brake
			        rightDrive(0.2);
			        wait1MSec(50);
			        halt();
			        
			        leftDrive(-.7);//straight 2
			        rightDrive(-.7);
			        wait1MSec(650);
			        leftDrive(0.2);//counter brake
			        rightDrive(0.2);
			        wait1MSec(100);
			        halt();
			        
			        leftDrive(.7);//turn 2
			        rightDrive(-.35);
			        wait1MSec(300);
			        leftDrive(0.2);//counter brake
			        rightDrive(0.2);
			        wait1MSec(50);
			        halt();
			        
			       				
				}
				
				leftDrive(-.7);//straight 3
		        rightDrive(-.7);
		        wait1MSec(900);
		        leftDrive(0.2);//counter brake
		        rightDrive(0.2);
		        wait1MSec(50);
		        halt();
		        rightDrive(-0.2); // We know that the robot is going to slam into the switch and bounce. This should keep it against the switch.
		        leftDrive(-0.2);
		        wait1MSec(500);
		        
				frontLeftIntake.set(0.7);
			    frontRightIntake.set(-0.7);
			    backRightIntake.set(-0.7);
			    backLeftIntake.set(0.7);
			    wait1MSec(4000);
			    frontLeftIntake.set(0);
			    frontRightIntake.set(0);
			    backRightIntake.set(0);
			    backLeftIntake.set(0);
			    autoEnabled = false;	
			}
			else if(stationNumber == 3) {
				wait1MSec((long)autoDelay);
		        leftDrive(-0.75);
		        rightDrive(-0.72);
		        wait1MSec(2000);
				leftDrive(0.2);//counter brake
		        rightDrive(0.2);
		        wait1MSec(100);
		        halt();
		        autoEnabled = false;
			}
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		// This exact code is also in disabledPeriodic, but I want to have control of it in teleop as well.
		driverSelect = SmartDashboard.getBoolean("DB/Button 0", false);
		if (driverSelect == false) {
			SmartDashboard.putString("DB/String 2", "Driver: Clark (Tank)");
		}
		else {
			SmartDashboard.putString("DB/String 2", "Driver: Ishmam (GTA)");
		}
		
		
		if (driverSelect == false) { // Clark's tank controls
			// Clark's tank control
			double speed = 0.7;
			if (gamepad.getRawButton(5)) {
				speed = 0.3;
			}
			
			double leftSpeed = gamepad.getRawAxis(1);
			double rightSpeed = gamepad.getRawAxis(5);

			leftSpeed = leftSpeed*speed;
			rightSpeed = rightSpeed*speed;

			leftDrive(leftSpeed);
			rightDrive(rightSpeed);
			
			// setMotors(leftSpeed, rightSpeed); //JT: I replaced this with the function from the auto, to make the code consistent.
						 
			if (gamepad.getRawAxis(2) > 0.05) {
				frontLeftIntake.set(-.9);
				frontRightIntake.set(.9);
				backLeftIntake.set(-.9);
				backRightIntake.set(.9);
			} 
			else if (gamepad.getRawAxis(3) > 0.05) {
				frontLeftIntake.set(.9);
				frontRightIntake.set(-.9);
				backLeftIntake.set(.9);
				backRightIntake.set(-.9);
			} 
			else {
				frontLeftIntake.set(0);
				frontRightIntake.set(0);
				backLeftIntake.set(0);
				backRightIntake.set(0);
			}
		}
		
		
		else { // Ishmam's GTA controls
			// Ishmam's master throttle
			if (gamepad.getRawButton(1)) 
			{
				speedMaster = 0.25;
			} 
			else if (gamepad.getRawButton(2)) 
			{
				speedMaster = 0.7;
			}
			
			// Ishmam's drive control. The triggers give forward/reverse, and the stick turns
			if (gamepad.getRawAxis(3) >= 0.05) {
				frontLeftDrive.set((gamepad.getRawAxis(3) + gamepad.getRawAxis(4)) * speedMaster);
				backLeftDrive.set((gamepad.getRawAxis(3) + gamepad.getRawAxis(4)) * speedMaster);
				frontRightDrive.set(-(gamepad.getRawAxis(3) - gamepad.getRawAxis(4)) * speedMaster);
				backRightDrive.set(-(gamepad.getRawAxis(3) - gamepad.getRawAxis(4)) * speedMaster);
			}
			else if (gamepad.getRawAxis(2) >= 0.05)	{
				frontLeftDrive.set(-(gamepad.getRawAxis(2) + gamepad.getRawAxis(4)) * speedMaster);
				backLeftDrive.set(-(gamepad.getRawAxis(2) + gamepad.getRawAxis(4)) * speedMaster);
				frontRightDrive.set((gamepad.getRawAxis(2) - gamepad.getRawAxis(4)) * speedMaster);
				backRightDrive.set((gamepad.getRawAxis(2) - gamepad.getRawAxis(4)) * speedMaster);
			} 
			else {
				// Turning code
				frontLeftDrive.set(gamepad.getRawAxis(4) * .5);
				backLeftDrive.set(gamepad.getRawAxis(4) * .5);
				frontRightDrive.set(gamepad.getRawAxis(4) * .5);
				backRightDrive.set(gamepad.getRawAxis(4) * .5);
			}

			// Ishmam's intake code
			if (gamepad.getRawButton(9)) {
				frontLeftIntake.set(-.9);
				frontRightIntake.set(.9);
				backLeftIntake.set(-.9);
				backRightIntake.set(.9);
			} 
			else if (gamepad.getRawButton(10)) {
				frontLeftIntake.set(.9);
				frontRightIntake.set(-.9);
				backLeftIntake.set(.9);
				backRightIntake.set(-.9);
			}
			else {
				frontLeftIntake.set(0);
				frontRightIntake.set(0);
				backLeftIntake.set(0);
				backRightIntake.set(0);
			}
		}

	}
	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
