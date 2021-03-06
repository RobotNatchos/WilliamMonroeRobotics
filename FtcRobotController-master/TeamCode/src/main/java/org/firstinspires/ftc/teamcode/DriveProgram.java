package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Drive", group = "Main")
// Wheel Movement
public class DriveProgram extends LinearOpMode{

    public void runOpMode(){
        //region Variables Setup
        double motorSpeed = 2000;
        boolean dP = false;
        double leftstickX;
        double leftstickY;
        double direction;
        double magnitude;
        double fRight;
        double bRight;
        double bLeft;
        double fLeft;
        double turn;

        boolean isPrep = true;
        boolean servoBool = true;
        boolean servoRel = true;
        boolean servoBlockBool = true;
        boolean servoBlockRel = true;


        //region Set Up Variables for Timer
        int stage = 0;
        long time;
        long checkTime;
        long checkTimeEnd = 0;
        double servoPos = 0.98;
        double servoBlockPos = 0.88;
        //endregion
        //endregion

        //region Hardware Map
        DcMotorEx backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        DcMotorEx backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        DcMotorEx frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        DcMotorEx frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        DcMotor intakeMotor  = hardwareMap.get(DcMotor.class, "intakeMotor");
        DcMotorEx lineMotor = hardwareMap.get(DcMotorEx.class, "leverMotor");
        DcMotorEx lineMotor2 = hardwareMap.get(DcMotorEx.class, "backMotor");
        Servo dropServo = hardwareMap.get(Servo.class, "dropServo");
        Servo blockServo = hardwareMap.get(Servo.class, "blockServo");
        DcMotor wheelMotor = hardwareMap.get(DcMotor.class, "wheelMotor");
        DistanceSensor rangeSen = hardwareMap.get(DistanceSensor.class, "rangeSen");

        lineMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lineMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lineMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        lineMotor2.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //endregion

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        while (opModeIsActive()){


            double distance = rangeSen.getDistance(DistanceUnit.INCH);



            //region Motor Speed adjustment
            if (this.gamepad1.dpad_up && motorSpeed < 10000){
                if (!dP) {
                    motorSpeed = motorSpeed + 1000;
                    dP = true;
                }
            }
            else if (this.gamepad1.dpad_down && motorSpeed > 1000){
                if(dP == false) {
                    motorSpeed = motorSpeed - 1000;
                    dP = true;
                }
            }
            else {
                dP = false;
            }

            //endregion

            leftstickX = this.gamepad1.left_stick_x;
            leftstickY = -this.gamepad1.left_stick_y;
            
            turn = -this.gamepad1.right_stick_x;
            //region Math For Wheel Movement
            direction = Math.atan2(leftstickY, leftstickX);
            magnitude = Math.sqrt(Math.pow(leftstickX, 2) + Math.pow(leftstickY, 2)) * 1.5;
            
            fRight = (motorSpeed * (Math.sin(direction - 1.0/4.0 * Math.PI) * magnitude + turn));
            bLeft = (motorSpeed * (-Math.sin(direction - 1.0/4.0 * Math.PI) * magnitude + turn));
            bRight = (motorSpeed * (Math.sin(direction + 1.0/4.0 * Math.PI) * magnitude + turn));
            fLeft = (motorSpeed * (-Math.sin(direction + 1.0/4.0 * Math.PI) * magnitude + turn));

            /*
            if (fRight > 1 || fRight < -1){
                fLeft = (fLeft / Math.abs(fRight));
                fRight = (fRight / Math.abs(fRight));
                bRight = (bRight / Math.abs(fRight));
                bLeft = (bLeft / Math.abs(fRight));
            }
        
            if (fLeft > 1 || fLeft < -1){
                fLeft = (fLeft / Math.abs(fLeft));
                fRight = (fRight / Math.abs(fLeft));
                bLeft = (bLeft / Math.abs(fLeft));
                bRight = (bRight / Math.abs(fLeft));
            }
            */
            //endregion


            //region Intake Movement
            if (Math.abs(this.gamepad2.left_stick_y) > 0){
                
                intakeMotor.setPower(this.gamepad2.left_stick_y);
            }
            else{
                intakeMotor.setPower(0);
            }
            //endregion

            //region Manual Servo Movement
            if(servoBool && this.gamepad2.left_trigger > 0.5 && servoRel && stage == 0){
                servoPos = .8;
                servoBool = false;
                servoRel = false;
            }

            else if(!servoBool && this.gamepad2.left_trigger > 0.5 && servoRel && stage == 0){
                servoPos = .98;
                servoBool = true;
                servoRel = false;
            }
            if (this.gamepad2.left_trigger < 0.2 && servoRel == false && stage == 0) {
                servoRel = true;
            }

            if(servoBlockBool && this.gamepad2.right_trigger > 0.5 && servoBlockRel){
                servoBlockPos = .60;
                servoBlockBool = false;
                servoBlockRel = false;
            }

            else if(!servoBlockBool && this.gamepad2.right_trigger > 0.5 && servoBlockRel){
                servoBlockPos = .88;
                servoBlockBool = true;
                servoBlockRel = false;
            }
            if (this.gamepad2.right_trigger < 0.2 && servoBlockRel == false) {
                servoBlockRel = true;
            }

            if (this.gamepad1.right_trigger > 0.5){
                wheelMotor.setPower(-1.0);
            }
            else if (this.gamepad1.left_trigger > 0.5){
                wheelMotor.setPower(1.0);
            }
            else {
                wheelMotor.setPower(0);
            }

            //endregion

            //region Prep For Pickup
            if(isPrep && this.gamepad2.a && stage == 0){
                time = 500;
                checkTime =  System.currentTimeMillis();
                checkTimeEnd = checkTime + time;
                stage = 7;
                servoPos = 0.7;
                isPrep = false;
            }

            if (checkTimeEnd > System.currentTimeMillis() && opModeIsActive() && stage == 7) {
                lineMotor.setPower(1);
                lineMotor2.setPower(-0.55);
                telemetry.addData("Running", "True");
            }

            if (checkTimeEnd < System.currentTimeMillis() && opModeIsActive() && stage == 7){
                time = 30;
                checkTime =  System.currentTimeMillis();
                checkTimeEnd = checkTime + time;
                stage = 8;
                telemetry.addData("Running", "True");
            }


            if (checkTimeEnd < System.currentTimeMillis() && opModeIsActive() && stage == 8) {
                stage = 0;
                lineMotor.setPower(0);
                lineMotor2.setPower(0);
                telemetry.addData("Running", "False");
            }

            if(!isPrep && this.gamepad2.y && stage == 0) {
                time = 520;
                checkTime =  System.currentTimeMillis();
                checkTimeEnd = checkTime + time;
                lineMotor.setPower(0);
                lineMotor2.setPower(0);
                servoPos = 0.98;
                stage = 9;
                isPrep = true;
            }


            //while timer is active and stage 4 is active move arm down
            if (checkTimeEnd > System.currentTimeMillis() && opModeIsActive() && stage == 9) {
                lineMotor.setPower(-0.55);
                lineMotor2.setPower(1);
                telemetry.addData("Running", "True");
            }

            if (checkTimeEnd < System.currentTimeMillis() && opModeIsActive() && stage == 9){
                time = 30;
                checkTime =  System.currentTimeMillis();
                checkTimeEnd = checkTime + time;
                stage = 10;
                telemetry.addData("Running", "True");
            }

            if (checkTimeEnd < System.currentTimeMillis() && opModeIsActive() && stage == 10){
                stage = 0;
                lineMotor.setPower(0);
                lineMotor2.setPower(0);
                telemetry.addData("Running", "False");
            }
            //endregion

            //region Stage Movement of arm
            //when X is clicked start timer and switch
            if (this.gamepad2.x && stage == 0 && isPrep){
                lineMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                lineMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                time = 575;
                checkTime = System.currentTimeMillis();
                checkTimeEnd = checkTime + time;
                servoBlockPos = .88;
                stage = 1;
            }

            //while timer is active and stage 1 is active move arm up
            if (checkTimeEnd > System.currentTimeMillis() && opModeIsActive() && stage == 1) {
                lineMotor.setPower(1);
                lineMotor2.setPower(-0.55);
                telemetry.addData("Running", "True");
            }


            //when time exceeds limit, start new time, switch stage, and move servo
            if (checkTimeEnd < System.currentTimeMillis() && opModeIsActive() && stage == 1) {
                time = 2800;
                checkTime = System.currentTimeMillis();
                checkTimeEnd = checkTime + time;
                lineMotor.setPower(0);
                lineMotor2.setPower(0);
                servoPos = 0.8;
                stage = 2;
            }

            //while timer is active and stage 2 is active move arm up
            if (checkTimeEnd > System.currentTimeMillis() && opModeIsActive() && stage == 2) {
                lineMotor.setPower(1);
                lineMotor2.setPower(-0.45);
                telemetry.addData("Running", "True");
            }

            /* when time exceeds limit, start new time, switch stage, and move servo
            (holds in same spot for a while) */
            if (checkTimeEnd < System.currentTimeMillis() && opModeIsActive() && stage == 2) {
                time = 1400;
                checkTime = System.currentTimeMillis();
                checkTimeEnd = checkTime + time;
                lineMotor.setPower(0);
                lineMotor2.setPower(0);
                servoPos = 0.10;
                stage = 12;
            }

            if (checkTimeEnd < System.currentTimeMillis() && opModeIsActive() && stage == 12 && this.gamepad2.b) {
                time = 500;
                checkTime = System.currentTimeMillis();
                checkTimeEnd = checkTime + time;
                lineMotor.setPower(0);
                lineMotor2.setPower(0);
                servoPos = 0.10;
                servoBlockPos = 0.4;
                stage = 3;
            }

            //when time exceeds limit, start new time, switch stage, and move servo
            if (checkTimeEnd < System.currentTimeMillis() && opModeIsActive() && stage == 3) {
                time = 2600;
                checkTime = System.currentTimeMillis();
                checkTimeEnd = checkTime + time;
                lineMotor.setPower(0);
                lineMotor2.setPower(0);
                servoPos = 0.8;
                sleep(20);
                stage = 4;
            }

            //while timer is active and stage 4 is active move arm down
            if (checkTimeEnd > System.currentTimeMillis() && opModeIsActive() && stage == 4) {
                lineMotor.setPower(-0.55);
                lineMotor2.setPower(1);
                telemetry.addData("Running", "True");
            }

            //when time exceeds limit, start new time, switch stage, and move servo
            if (checkTimeEnd < System.currentTimeMillis() && opModeIsActive() && stage == 4) {
                time = 650;
                checkTime = System.currentTimeMillis();
                checkTimeEnd = checkTime + time;
                lineMotor.setPower(0);
                lineMotor2.setPower(0);
                servoPos = 0.98;
                servoBlockPos = .88;
                sleep(20);
                stage = 5;
            }

            //while timer is active and stage 5 is active move arm down
            if (checkTimeEnd > System.currentTimeMillis() && opModeIsActive() && stage == 5) {
                lineMotor.setPower(-0.55);
                lineMotor2.setPower(1);
                telemetry.addData("Running", "True");
            }

            if (checkTimeEnd < System.currentTimeMillis() && opModeIsActive() && stage == 5) {
                time = 30;
                checkTime = System.currentTimeMillis();
                checkTimeEnd = checkTime + time;
                lineMotor.setPower(0);
                lineMotor2.setPower(0);
                stage = 0;
                telemetry.addData("Running", "True");
            }
            //endregion

            dropServo.setPosition(servoPos);
            blockServo.setPosition(servoBlockPos);

            //region Manual Motor And Arm Movement

            if(stage == 0 && !this.gamepad1.left_bumper && !this.gamepad1.right_bumper && !this.gamepad1.dpad_right && !this.gamepad1.dpad_left) {
                lineMotor.setPower(0);
                lineMotor2.setPower(0);
                telemetry.addData("Running", "False");
            }

            //region Manual Motor Movement for adjustment
            if (this.gamepad2.dpad_left){
                lineMotor2.setPower(-1.0);
                telemetry.addData("Running", "True");
            }
            if (this.gamepad2.dpad_right){
                lineMotor2.setPower(1.0);
                telemetry.addData("Running", "True");
            }
            if (this.gamepad2.right_bumper){
                lineMotor.setPower(1.0);
                telemetry.addData("Running", "True");
            }
            if (this.gamepad2.left_bumper){
                lineMotor.setPower(-1.0);
                telemetry.addData("Running", "True");
            }

            //endregion

            //region Arm Movement
            if (this.gamepad2.dpad_up){
                lineMotor.setPower(1.0);
                lineMotor2.setPower(-1.0);
                telemetry.addData("Running", "True");

            }
            if (this.gamepad2.dpad_down){
                lineMotor.setPower(-1.0);
                lineMotor2.setPower(1.0);
                telemetry.addData("Running", "True");

            }

            //endregion



            //endregion

            //region Setting Motors
            if (bLeft > 0){
                backLeft.setDirection(DcMotor.Direction.FORWARD);
            }
            else {
                backLeft.setDirection(DcMotor.Direction.REVERSE);
            }
            if (bRight > 0){
                backRight.setDirection(DcMotor.Direction.FORWARD);
            }
            else {
                backRight.setDirection(DcMotor.Direction.REVERSE);
            }
            if (fRight > 0){
                frontRight.setDirection(DcMotor.Direction.FORWARD);
            }
            else {
                frontRight.setDirection(DcMotor.Direction.REVERSE);
            }
            if (fLeft > 0){
                frontLeft.setDirection(DcMotor.Direction.REVERSE);
            }
            else {
                frontLeft.setDirection(DcMotor.Direction.FORWARD);
            }


            backLeft.setVelocity(Math.abs(bLeft));
            frontRight.setVelocity(Math.abs(fRight));
            backRight.setVelocity(Math.abs(bRight));
            frontLeft.setVelocity(Math.abs(fLeft));
            //endregion

            //region Telemetry Data
            telemetry.addData("motorSpeed", motorSpeed);
            telemetry.addData("Status", "Initialized");
            telemetry.addData("Arm Motor", lineMotor.getCurrentPosition());
            telemetry.addData("Distance:", distance);
            telemetry.addData("Stage", stage);
            telemetry.update();
            //endregion
        }
    }
    
}
