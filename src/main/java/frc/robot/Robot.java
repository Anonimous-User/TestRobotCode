// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

/** This is a demo program showing how to use Mecanum control with the MecanumDrive class. */
public class Robot extends TimedRobot {
  private static final int kFrontLeftChannel = 2;
  private static final int kRearLeftChannel = 0;
  private static final int kFrontRightChannel = 3;
  private static final int kRearRightChannel = 1;

  private static final int kJoystickChannel = 0;

  private MecanumDrive m_robotDrive;
  XboxController m_driverController = new XboxController(0);

  private PWMSparkMax frontLeft = new PWMSparkMax(kFrontLeftChannel);
  private PWMSparkMax rearLeft = new PWMSparkMax(kRearLeftChannel);
  private PWMSparkMax frontRight = new PWMSparkMax(kFrontRightChannel);
  private PWMSparkMax rearRight = new PWMSparkMax(kRearRightChannel);

  @Override
  public void robotInit() {

    // Invert the right side motors.
    // You may need to change or remove this to match your robot.
    frontRight.setInverted(false);
    rearRight.setInverted(true);

    m_robotDrive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);
  }

  @Override
  public void teleopPeriodic() {
    // Use the joystick Y axis for forward movement, X axis for lateral
    // movement, and Z axis for rotation.
    driveMecanum(m_driverController.getLeftX(), -m_driverController.getLeftY(), m_driverController.getRightX());
  }

  public void driveMecanum(double xSpeed, double ySpeed, double zRot) {
    // Calculate the angle and magnitude of the joystick input
    double theta = Math.atan2(ySpeed, xSpeed);
    double power = Math.hypot(xSpeed, ySpeed);
    
    // Calculate the sine and cosine of the angle, offset by 45 degrees
    double sin = Math.sin(theta - Math.PI / 4);
    double cos = Math.cos(theta - Math.PI / 4);
    double max = Math.max(Math.abs(sin), Math.abs(cos));

    // Calculate the motor powers for each wheel based on the joystick input
    double leftFront = power * cos / max + zRot;
    double rightFront = power * sin / max - zRot;
    double leftRear = power * sin / max + zRot;
    double rightRear = power * cos / max - zRot;

    // Scale the motor powers if necessary to avoid exceeding the maximum power
    if ((power + Math.abs(zRot)) > 1) {
      leftFront /= power + Math.abs(zRot);
      rightFront /= power + Math.abs(zRot);
      leftRear /= power + Math.abs(zRot);
      rightRear /= power + Math.abs(zRot);
    }

    if(leftFront==0&&leftRear==0&&rightFront==0&&rightRear==0){
      this.frontLeft.stopMotor();
      this.rearLeft.stopMotor();
      this.rearRight.stopMotor();
      this.frontRight.stopMotor();
    } else{
      frontLeft.set(leftFront*.8);
      rearLeft.set(leftRear*.8);
      rearRight.set(rightRear*.8);
      frontRight.set(rightFront*.8);
    }
  }
}
