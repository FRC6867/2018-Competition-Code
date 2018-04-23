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
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Compressor; //JT: This is only needed if running an onboard compressor
import edu.wpi.first.wpilibj.DoubleSolenoid; //JT: And this allows control for the solenoids that are running the actuators


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


	// Declare the drive motors. These are all now on the Talons. They were on the Victors in old builds
	WPI_TalonSRX frontLeftDrive = new WPI_TalonSRX(11);
	WPI_TalonSRX backLeftDrive = new WPI_TalonSRX(10);
	WPI_TalonSRX frontRightDrive = new WPI_TalonSRX(21);
	WPI_TalonSRX backRightDrive = new WPI_TalonSRX(20);
	
	/*Old config
	// Declare the drive motors. They're all on Victor motor controllers
	Victor frontLeftDrive=new Victor (1);	
	Victor backLeftDrive=new Victor (0);
	Victor frontRightDrive=new Victor (3);
	Victor backRightDrive=new Victor (2);
	Victor lights=new Victor (4); // blinkin LED strip controller
	*/


	/*Old config
	// Delcare the intake motors. They're on the TalonSRX controllers
	WPI_TalonSRX frontLeftIntake = new WPI_TalonSRX(10);
	WPI_TalonSRX frontRightIntake = new WPI_TalonSRX(20);
	WPI_TalonSRX backRightIntake = new WPI_TalonSRX(21);
	WPI_TalonSRX backLeftIntake = new WPI_TalonSRX(11);	
	*/
	
	// Delcare the intake motors. These are now on the Victors. They used to be Talons.
	Victor frontLeftIntake =new Victor (1);	
	Victor backLeftIntake = new Victor (0);
	Victor frontRightIntake = new Victor (3);
	Victor backRightIntake = new Victor (2);
	Victor lights=new Victor (4);
	
	// Declare the controller. We're using the Logitech gamepad
	Joystick gamepad=new Joystick (0);
	
	// Declare the encoders. It's about 46 encoder ticks per inch.
	Encoder leftEncoder;
	Encoder rightEncoder;

	// JT: None of this code can be tested until we get our hands on a Rio and wire up the systems.
	// JT: The compressor is probably best running off a closed-loop feedback system from the PCM, but we'll need to calibrate it
	//Compressor c = new Compressor(0);
	//DoubleSolenoid leftSolenoid = new DoubleSolenoid(1,2); // JT: The double solenoids have a forward and a reverse channel, so they're each going to take up two pairs of ports on the PCM
	//DoubleSolenoid rightSolenoid = new DoubleSolenoid(3,4); // JT: This isn't actually wired yet, so these channels will need to be checked
	
	

	boolean autoEnabled = true; // This flag will let us prevent the periodic auto from looping
	boolean lightSwitch = true; // JT: We want to activate the lights once per cycle, at the start of the cycle. So every time we start a new mode this should run once.
	boolean isDisabled = false; //JT: This is a flag to help with communicating when the robot is disabled.
	long autoDelay = 0; // This allows us to delay the start of our autonomous, in case our alliance needs us to wait.
	boolean driverSelect = false; // We can use this to toggle between Ishmam and Clark's driver code
	
	double speedMaster = 0.7; // Used in driver code to cap the motor speeds
	
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		CameraServer.getInstance().startAutomaticCapture();
		// JT: Again, this is setting up the encoders.
		// JT: Declare the encoders. This is untested. I'm guessing the ports, which side is which, and what's reversed.
		// JT: DO NOT EVEN THINK OF USING THE ENCODERS IN COMPETITION WITHOUT VERIFYING THIS FIRST
		leftEncoder = new Encoder(0,1,false,Encoder.EncodingType.k4X);  // Both sides are seem to be counting correctly right now, but I may have left and right backwards (which would mean the encoders should be reversed)
		rightEncoder = new Encoder(2,3,true,Encoder.EncodingType.k4X); 
		lightingControl(); //enables the lights
	}
	
	public void lightingControl() { //lighting control for the strips
		DriverStation.Alliance color;
		color = DriverStation.getInstance().getAlliance();
		/*
		  if(color == DriverStation.Alliance.Blue && isDisabled()){ //blue disabled
		 
			lights.set(0.09);//breath slow
		}
		else if(color == DriverStation.Alliance.Blue && isEnabled()){ //blue enabled
			lights.set(0.01);//light chase
		}
		else if(color == DriverStation.Alliance.Red && isDisabled()){ //red disabled
			lights.set(0.29); //breath slow
		}
		else if(color == DriverStation.Alliance.Red && isEnabled()){ //red enabled
			lights.set(0.21);//light chase
		
		}
		else {
			lights.set(-0.23);//heartbeat blue
		}*/
		lights.set(-0.15);
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
	
	public void intake(double speed) {
		frontLeftIntake.set(speed);
		frontRightIntake.set(-speed);
		backLeftIntake.set(speed);
		backRightIntake.set(-speed);
	}
	/*
	public void externalIntake() {
		// JT: Tentatively, the idea would be to spin up the external intake's wheels and then clamp the claw
		// What motor controllers are we using? The sparks?
		// Until we know what motor controllers we're on we can't really declare the motors.
		// leftExternalIntake.set(.9);
		// rightExternalIntake.set(-.9);
		wait1MSec(500);
		leftSolenoid.set(DoubleSolenoid.Value.kForward);
		rightSolenoid.set(DoubleSolenoid.Value.kForward);
	}
	*/
	public void halt() {
		// A handy function to stop drive functions. Includes a 50ms delay to help kill momentum before moving on.
		frontLeftDrive.set(0);
		backLeftDrive.set(0);
		frontRightDrive.set(0);
		backRightDrive.set(0);
		wait1MSec(50);
	}

	public void driveForDistance(double ticks, double speed) {
		// JT: This is an experimental feature. In theory it'll cause the robot to drive forward (straight) a desired distance at a desired speed
		// JT: It's just bang-bang control. Ideally this would use proportional control and also have smooth accelerations, but this is more of a model for the programming team to figure out later.
		// JT: This is also only going to work when going forward.
		double slowApproach = 1;
		halt(); // JT: Just a little safety thing. It's easier to do this from a stopped position.
		leftEncoder.reset();
		rightEncoder.reset(); // JT: Need to reset encoders to know where we're starting from
		while(leftEncoder.getRaw() < ticks) // JT: It's also possible to use have the encoder library calculate a distance for us. Or we can do the math. For now the tick argument is going to seem like a crazy number.
		{
			if(leftEncoder.getRaw() > (0.7 * ticks))
			{
				slowApproach = 0.6;
			}
			else if(leftEncoder.getRaw() > (0.8 * ticks))
			{
				slowApproach = 0.5;
			}
			else if(leftEncoder.getRaw() > (0.9 * ticks))
			{
				slowApproach = 0.4;
			}
			if(leftEncoder.getRaw() > rightEncoder.getRaw()) {
				leftDrive(speed * slowApproach * 0.6);
				rightDrive(speed * slowApproach);
			}
			else if(rightEncoder.getRaw() > leftEncoder.getRaw()) {
				leftDrive(speed * slowApproach);
				rightDrive(speed* slowApproach * 0.6);
			}
			else {
				leftDrive(speed * slowApproach);
				rightDrive(speed * slowApproach);
			}
			System.out.println("leftEncoder: " + Double.toString(leftEncoder.getRaw())); // JT: These lines can be commented out later. They're here for now to at least debug this.
			System.out.println("rightEncoder: " + Double.toString(rightEncoder.getRaw()));			
		}
		halt(); // JT: This is going to skid. Again, ideally this would have smooth accelerations, but it's a start.
	}
	
	public void wait1MSec(long time){
		// wait1MSec exists only to mimic a function that's familiar to anyone with Vex/RobotC experience.
		// This will pause execution of code for a set duration (in milliseconds), allowing for simple drive-for-time behaviours
		long Time0 = System.currentTimeMillis();
	    long Time1;
	    long runTime = 0;
	    while(runTime<time){
	        Time1 = System.currentTimeMillis();
	        runTime = Time1 - Time0;
	        //System.out.println("Our runtime: " + Long.toString(runTime)); // TODO: remove me please
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
		//lights.set(-0.15);
		//lightingControl();
		//if (lightSwitch == true && isDisabled == false) { 
		//	//isDisabled defaults to false, and is set to false at the beginning of each op mode. It isn't set to true until *after* this if-statement, so this should only run once
		//	lightingControl(); // JT: Activate lights during autonomous
		//	lightSwitch = true; //JT: This is also different from the ones in TeleOp and Auto Periodic. I need to leave this "on" so that those lights will activate, but I prevent it from re-activating here by using the isDisabled flag
		//}
		
		autoEnabled = true; //JT: Reset the enabled flag so that we can run auto by disabling/re-enabling auto.
		


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
		else if (gamepad.getRawButton(4)) {
			stationNumber = 4;
			SmartDashboard.putString("DB/String 0", "Auto: !!!TEST!!!");
		}

		
		// This will read a value off of the first slider, and use it to delay the start of autonomous up to 5s.
		// The dashboard returns a double, but we want a long
		double d = SmartDashboard.getNumber("DB/Slider 0", 0.0) * 3000; // It can read a value from 0 to 5, but we need a value in millisecs
		autoDelay = new Double(d).longValue(); 
		SmartDashboard.putString("DB/String 1", "autoDelay: " + Long.toString(autoDelay) + "ms"); //JT: This is outputting a whole number, it works...
		
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
		//lights.set(-0.15);
		//lightingControl();
		//if (lightSwitch = true) {
		//	lightingControl(); // JT: Activate lights during autonomous
		//	lightSwitch = false; //JT: Prevents the lights from being reset repeatedly
		//}

		//int stationNumber = DriverStation.getInstance().getLocation();  //I actually don't know if the stations are 0/1/2 or 1/2/3 
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();

		System.out.println("Our autoDelay for this run: " + Long.toString(autoDelay));
		//autoDelay=autoDelay*1; //dummy line to see how autoDelay is working. 
		wait1MSec(autoDelay); // JT: This defaults to zero, but can be used to delay our autonomous
		
		if(gameData.length() > 0 && autoEnabled == true) { // Call red 0? Sake of argument?
			if(stationNumber == 1) {
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
		        leftDrive(-0.75);
		        rightDrive(-0.72);
		        wait1MSec(2000);
				leftDrive(0.2);//counter brake
		        rightDrive(0.2);
		        wait1MSec(100);
		        halt();
		        autoEnabled = false;
			}
			else if(stationNumber == 4) { //JT: Drive for 1000 ticks, whatever that is, at half speed. Then go into a debugging loop so that we can see what the encoders are doing.
				driveForDistance(5888,0.3); 
				autoEnabled = false;
			}
		}
	}

	
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		//lights.set(-0.15);
		//lightingControl();
		//if (lightSwitch == true) {
		//	lightingControl(); //JT: Activate lights for TeleOp
		//	lightSwitch = false; // JT: If we don't do this we'll keep resetting the light mode over and over
		//}


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
			if (gamepad.getRawButton(5)) {
				speedMaster = 0.3;
			}
			else {
				speedMaster = 0.8;
			}
				
			// JT: Clark's controls treat the back of the robot as the front, and the front as the back.
			double leftSpeed = - gamepad.getRawAxis(1);
			double rightSpeed = - gamepad.getRawAxis(5);

			leftSpeed = leftSpeed*speedMaster;
			rightSpeed = rightSpeed*speedMaster;

			leftDrive(leftSpeed);
			rightDrive(rightSpeed);
						 
			if (gamepad.getRawAxis(2) > 0.05) {
				intake(-.9);
			} 
			else if (gamepad.getRawAxis(3) > 0.05) {
				intake(.9);
			} 
			else {
				intake(0);
			}
			if (gamepad.getRawButton(1)) { //Just some debugging code so that I can get the encoder values from console
				System.out.println("leftEncoder: " + Double.toString(leftEncoder.getRaw())); 
				System.out.println("rightEncoder: " + Double.toString(rightEncoder.getRaw()));
			}
			else if (gamepad.getRawButton(2)) {
				leftEncoder.reset();
				rightEncoder.reset();
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
				intake(-.9);
			} 
			else if (gamepad.getRawButton(10)) {
				intake(.9);
			}
			else {
				intake(0);
			}
		}

	}
	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		//isDisabled = false;
	}
}
