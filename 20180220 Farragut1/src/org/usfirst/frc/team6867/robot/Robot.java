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

	// Declare the drive motors. They're all on Victor motor controllers
	Victor frontleftdrive=new Victor (1);	
	Victor rearleftdrive=new Victor (0);
	Victor frontrightdrive=new Victor (3);
	Victor rearrightdrive=new Victor (2);

	// Delcare the intake motors. They're on the TalonSRX controllers
	WPI_TalonSRX _frontLeftMotor = new WPI_TalonSRX(10);
	WPI_TalonSRX _frontRightMotor = new WPI_TalonSRX(20);
	WPI_TalonSRX _backRightMotor = new WPI_TalonSRX(21);
	WPI_TalonSRX _backLeftMotor = new WPI_TalonSRX(11);	
	
	// Declare the controller. We're using the Logitech gamepad
	Joystick controller=new Joystick (0);

	boolean autoEnabled = true;
	
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
	
	public void rightDrive(double speed) {
		frontrightdrive.set(speed);
		rearrightdrive.set(speed);
	}
	
	public void leftDrive(double speed) {
		frontleftdrive.set(-speed);
		rearleftdrive.set(-speed);
	}

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

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		int stationNumber = DriverStation.getInstance().getLocation();  //I actually don't know if the stations are 0/1/2 or 1/2/3 
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();

		
		
		if(gameData.length() > 0 && autoEnabled == true) { // Call red 0? Sake of argument?
			if(stationNumber == 1) {
				leftDrive(.75);//forward
				rightDrive(.72);
				wait1MSec(2000);
		        leftDrive(-0.2);//counter brake
		        rightDrive(-0.2);
		        wait1MSec(100);
		        leftDrive(0);//stop
		        rightDrive(0);
		        autoEnabled = false;
		    
			}
			
			else if(stationNumber == 2) {
		        leftDrive(.7);//straight 1
		        rightDrive(.7);
		        wait1MSec(400);
		        leftDrive(-0.2);//counter brake
		        rightDrive(-0.2);
		        wait1MSec(50);
		        leftDrive(0);//stop
		        rightDrive(0);
		        wait1MSec(50);
		        
				if(gameData.charAt(0) == 'R') {// to the right side of switch
    
			        leftDrive(-.45);//turn 1
			        rightDrive(.7);
			        wait1MSec(400);
			        leftDrive(-0.2);//counter brake
			        rightDrive(-0.2);
			        wait1MSec(50);
			        leftDrive(0);//stop
			        rightDrive(0);
			        wait1MSec(50);
			        
			        leftDrive(.7);//straight 2
			        rightDrive(.7);
			        wait1MSec(570);
			        leftDrive(-0.2);//counter brake
			        rightDrive(-0.2);
			        wait1MSec(50);
			        leftDrive(0);//stop
			        rightDrive(0);
			        wait1MSec(50);
			        
			        leftDrive(.35);//turn 2
			        rightDrive(-.7);
			        wait1MSec(695);
			        leftDrive(-0.2);//counter brake
			        rightDrive(-0.2);
			        wait1MSec(50);
			        leftDrive(0);//stop
			        rightDrive(0);
			        wait1MSec(50);
			        
			        		 			
				} 
				else if(gameData.charAt(0) == 'L'){ //left side of the switch
			     
			        leftDrive(.45);//turn 1
			        rightDrive(-.7);
			        wait1MSec(550);
			        leftDrive(-0.2);//counter brake
			        rightDrive(-0.2);
			        wait1MSec(50);
			        leftDrive(0);//stop
			        rightDrive(0);
			        wait1MSec(50);
			        
			        leftDrive(.7);//straight 2
			        rightDrive(.7);
			        wait1MSec(500);
			        leftDrive(-0.2);//counter brake
			        rightDrive(-0.2);
			        wait1MSec(100);
			        leftDrive(0);//stop
			        rightDrive(0);
			        wait1MSec(50);
			        
			        leftDrive(-.7);//turn 2
			        rightDrive(.35);
			        wait1MSec(300);
			        leftDrive(-0.2);//counter brake
			        rightDrive(-0.2);
			        wait1MSec(50);
			        leftDrive(0);//stop
			        rightDrive(0);
			        wait1MSec(50);
			        
			       				
				}
				
				leftDrive(.7);//straight 3
		        rightDrive(.7);
		        wait1MSec(900);
		        leftDrive(-0.2);//counter brake
		        rightDrive(-0.2);
		        wait1MSec(50);
		        leftDrive(0);//stop
		        rightDrive(0);
		        wait1MSec(1000);
		        
				_frontLeftMotor.set(0.7);
			    _frontRightMotor.set(-0.7);
			    _backRightMotor.set(-0.7);
			    _backLeftMotor.set(0.7);
			    wait1MSec(4000);
			    _frontLeftMotor.set(0);
			    _frontRightMotor.set(0);
			    _backRightMotor.set(0);
			    _backLeftMotor.set(0);
			    autoEnabled = false;	
			}
			else if(stationNumber == 3) {
		        leftDrive(0.75);
		        rightDrive(0.72);
		        wait1MSec(2000);
				leftDrive(-0.2);//counter brake
		        rightDrive(-0.2);
		        wait1MSec(100);
		        leftDrive(0);//stop
		        rightDrive(0);
		        autoEnabled = false;
			}
		}
		}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		frontleftdrive.set(controller.getRawAxis(1));
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
