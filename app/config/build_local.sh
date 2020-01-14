#!/bin/bash
WORKING_DIR="../insta-downloader"

SDK_DIR='../sdk/build-tools'
BUILD_TOOL_DIR='28.0.3'
KEYSTORE_FILE="$WORKING_DIR/app/config/example.jks"
KEYSTORE_PASS="$WORKING_DIR/app/config/example.txt"
UNSIGNED_FILE_NAME=app-release.apk

function lintProject {

  if [ -d $WORKING_DIR ]; then
      echo "DIRECTORY '$WORKING_DIR' EXISTS."
  else
      echo "Error: DIRECTORY '$WORKING_DIR' DOES NOT EXISTS."
      exit 9999
  fi

  echo "START LINT PROJECT..."
  $WORKING_DIR/gradlew lint
  echo "END LINT PROJECT"
}

function build {

  if [ -d $WORKING_DIR ]; then
      echo "DIRECTORY '$WORKING_DIR' EXISTS."
  else
      echo "Error: DIRECTORY '$WORKING_DIR' DOES NOT EXISTS."
      exit 9999
  fi

  echo "START CLEANING PROJECT..."
	$WORKING_DIR/gradlew clean
	echo "PROJECT CLEANED: $WORKING_DIR"

	echo "START BUILDING PROJECT..."
	if [ $1 = "--debug" -o $1 = "-d" ]; then
		$WORKING_DIR/gradlew assembleDebug --stacktrace
	else
    $WORKING_DIR/gradlew assembleRelease --stacktrace
		if [ -f $WORKING_DIR/app/build/outputs/apk/release/$UNSIGNED_FILE_NAME ]; then
			signApk
		else
			echo "file $WORKING_DIR/app/build/outputs/apk/release/$UNSIGNED_FILE_NAME not found"
		fi
	fi
	echo "PROJECT BUILD FINISHED."
}

function signApk {
	echo "START SIGNING APK..."
	TIMESTAMP=$(date +"%Y_%m_%d_%H%M%S")
  SIGNED_FILE_NAME=TGT_User_Live_Build_$TIMESTAMP.apk

	$SDK_DIR/$BUILD_TOOL_DIR/zipalign -v -p 4 $WORKING_DIR/app/build/outputs/apk/release/$UNSIGNED_FILE_NAME $WORKING_DIR/app/build/outputs/apk/release/$SIGNED_FILE_NAME
	$SDK_DIR/$BUILD_TOOL_DIR/apksigner sign --ks $KEYSTORE_FILE --ks-pass file:$KEYSTORE_PASS $WORKING_DIR/app/build/outputs/apk/release/$SIGNED_FILE_NAME
	echo "SIGN APK FINISHED."
}

build $1