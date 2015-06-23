#!/bin/bash

#================================================================#
# Copyright (c) 2010-2011 Zipline Games, Inc.
# All Rights Reserved.
# http://getmoai.com
#================================================================#

	function cp777 () {
		cp $1 $2 $3
		chmod 777 $2
	}

	set -e
	
	channels=(downjoy uc nd91 qihoo360 xiaomi duoku cmgc ultralisk xianguo yuzhuo)
	
	for ch in "${channels[@]}"; do
		if [ x"$ch" != x"$1" ]; then
			channel_name=$1
			channel_lua_name=$2
			sub_channel_id=$3
			use_all_in_one=$4
		fi
	done
	
	# check for command line switches
	usage="usage: $0 [-r localRootFolder]"
	local_root=
	
	while [ $# -gt 0 ];	do
	    case "$1" in
	        -r)  local_root="$2"; shift;;
			-*)
		    	echo >&2 \
		    		$usage
		    	exit 1;;
			*)  break;;	# terminate while loop
	    esac
	    shift
	done

	package=cn.ultralisk.gameapp
	package_path=src/cn/ultralisk/gameapp
	out_dir="`pwd`/build_$channel_name"
	
	rm -rf $out_dir

	source ./settings-global.sh
	source ./settings-local.sh
	# SDK 
	host_source=$MOAI14_SDK/and/host-source
	
	app_package=$package.$pkg_name

	if [ x"$android_sdk_root" != x ] && [ x"$local_root" != x ] && [[ ! $android_sdk_root == /* ]]; then
		android_sdk_root=$local_root/$android_sdk_root
	fi
	
	if [ x"$android_sdk_root" = x ] || [ ! -d $android_sdk_root ]; then		
		echo -e "*** Please specify a valid path to the Android SDK in \"settings-local.sh\""
		echo
		exit 1
	fi

	mkdir -p $out_dir/project

	mkdir -p $out_dir/project/assets

	mkdir -p $out_dir/project/libs
	cp -fR	$host_source/project/libs/*	$out_dir/project/libs
	chmod 777 -R $host_source/project/libs/*
	mkdir -p $out_dir/project/res
	cp -fR	$host_source/project/res/*	$out_dir/project/res
	chmod 777 -R $host_source/project/res/*
	mkdir -p $out_dir/project/res/drawable-ldpi
	mkdir -p $out_dir/project/res/drawable-mdpi
	mkdir -p $out_dir/project/res/drawable-hdpi
	mkdir -p $out_dir/project/res/drawable-xhdpi
	mkdir -p $out_dir/project/res/raw

	if [ x"$icon_ldpi" != x ] && [ -f $icon_ldpi ]; then
		cp777 -f $icon_ldpi $out_dir/project/res/drawable-ldpi/icon.png
	fi

	if [ x"$icon_mdpi" != x ] && [ -f $icon_mdpi ]; then
		cp777 -f $icon_mdpi $out_dir/project/res/drawable-mdpi/icon.png
	fi

	if [ x"$icon_hdpi" != x ] && [ -f $icon_hdpi ]; then
		cp777 -f $icon_hdpi $out_dir/project/res/drawable-hdpi/icon.png
	fi

	if [ x"$icon_xhdpi" != x ] && [ -f $icon_xhdpi ]; then
		cp777 -f $icon_xhdpi $out_dir/project/res/drawable-xhdpi/icon.png
	fi
	
	if [ x"$key_store" != x ] && [ x"$local_root" != x ] && [[ ! $key_store == /* ]]; then
		key_store=$local_root/$key_store
	fi
	
	if [ x"$key_store" != x ] && [ -f $key_store ]; then
		cp777 -f $key_store $out_dir/project/`basename $key_store`
	fi
			
	cp777 -f $host_source/project/.classpath $out_dir/project/.classpath
	cp777 -f $host_source/project/proguard.cfg $out_dir/project/proguard.cfg
	
	mkdir -p $out_dir/project/$package_path
	
	backup_ext=.backup
	
	function fr () {
		sed -i$backup_ext s%"$2"%"$3"%g $1
		rm -f $1$backup_ext
		chmod 777 $1
	}
	
	fr $out_dir/project/res/values/strings.xml @NAME@ "$app_name"
	
	cp777 -f $host_source/project/.project $out_dir/project/.project 
	fr $out_dir/project/.project @NAME@ "$project_name"

	cp777 -f $host_source/project/build.xml $out_dir/project/build.xml
	#cdsc add start
	pack_type=NoRes
	if [ x"$use_all_in_one" == xtrue ]; then
		pack_type=AllInOne
	fi
	apk_name=$project_name"_V"$version_name"_"$pack_type"_"$channel_lua_name
	cp777 -f key/"$channel_name"_key  $out_dir/project/"$channel_name"_key
	#cdsc add end
	fr $out_dir/project/build.xml @NAME@ "$apk_name"

	cp777 -f $host_source/project/AndroidManifest.xml $out_dir/project/AndroidManifest.xml
	fr $out_dir/project/AndroidManifest.xml	@DEBUGGABLE@ "$debug"
	fr $out_dir/project/AndroidManifest.xml	@VERSION_CODE@ "$version_code"
	fr $out_dir/project/AndroidManifest.xml	@VERSION_NAME@ "$version_name"	
	fr $out_dir/project/AndroidManifest.xml	@APP_PACKAGE@ "$app_package"	
	cp777 -f $host_source/project/ant.properties $out_dir/project/ant.properties
	if [ x"$key_store" != x ]; then
		key_store=`basename $key_store`
	fi
	fr $out_dir/project/ant.properties @KEY_STORE@ "$key_store"
	fr $out_dir/project/ant.properties @KEY_ALIAS@ "$key_alias"
	fr $out_dir/project/ant.properties @KEY_STORE_PASSWORD@ "$key_store_password"
	fr $out_dir/project/ant.properties @KEY_ALIAS_PASSWORD@ "$key_alias_password"

	cp777 -f $host_source/project/project.properties $out_dir/project/project.properties

	dependency_index=1
	for (( i=0; i<${#requires[@]}; i++ )); do
		library=${requires[$i]}
		if ! [[ $library =~ ^[a-zA-Z0-9_\-]+$ ]]; then
			echo -e "*** Illegal optional component specified: $library, skipping..."
			echo -e "    > Optional component references may only contain letters, numbers, dashes and underscores"
			echo
			continue
		fi
		if [ -f $host_source/external/$library/manifest_declarations.xml ]; then
			awk 'FNR==NR{ _[++d]=$0; next } /EXTERNAL DECLARATIONS:/ { print; print ""; for ( i=1; i<=d; i++ ) { print _[i] } next } 1' $host_source/external/$library/manifest_declarations.xml $out_dir/project/AndroidManifest.xml > /tmp/AndroidManifest.tmp && mv -f /tmp/AndroidManifest.tmp $out_dir/project/AndroidManifest.xml
		fi
		if [ -f $host_source/external/$library/manifest_permissions.xml ]; then
			awk 'FNR==NR{ _[++d]=$0; next } /EXTERNAL PERMISSIONS:/ { print; print ""; for ( i=1; i<=d; i++ ) { print _[i] } next } 1' $host_source/external/$library/manifest_permissions.xml $out_dir/project/AndroidManifest.xml > /tmp/AndroidManifest.tmp && mv -f /tmp/AndroidManifest.tmp $out_dir/project/AndroidManifest.xml
		fi
		if [ -f $host_source/external/$library/classpath.xml ]; then
			awk 'FNR==NR{ _[++d]=$0; next } /EXTERNAL ENTRIES:/ { print; print ""; for ( i=1; i<=d; i++ ) { print _[i] } next } 1' $host_source/external/$library/classpath.xml $out_dir/project/.classpath > /tmp/.classpath.tmp && mv -f /tmp/.classpath.tmp $out_dir/project/.classpath
		fi
		#cdsc add start
		# replace again after merge manifest
		fr $out_dir/project/AndroidManifest.xml	@APP_PACKAGE@ "$app_package"
		fr $out_dir/project/AndroidManifest.xml	@JPUSH_APP_KEY@ "$jpush_appkey"
		fr $out_dir/project/AndroidManifest.xml	@TALKINGDATA_APPID@ "$talkingdata_appid"
		fr $out_dir/project/AndroidManifest.xml	@TALKINGDATA_CHANNELID@ "$talkingdata_channelid"
		#cdsc add end
		if [ -d $host_source/moai/$library ]; then
#			rsync -r --exclude=.svn --exclude=.DS_Store "$host_source/moai/$library/." "$out_dir/project/src/com/ziplinegames/moai"
			chmod 777 -R $host_source/external/$library/*
			pushd $host_source/moai/$library > /dev/null
				find . -name ".?*" -type d -prune -o -type f -print0 | cpio -pmd0 --quiet $out_dir/project/src/com/ziplinegames/moai
			popd > /dev/null
			chmod 777 -R $out_dir/project/src/com/ziplinegames/moai/*
		fi
		if [ -d $host_source/external/$library/project ]; then
#			rsync -r --exclude=.svn --exclude=.DS_Store "$host_source/external/$library/project/" "$out_dir/$library"
			chmod 777 -R $host_source/external/$library/project/*
			pushd $host_source/external/$library/project > /dev/null
				find . -name ".?*" -type d -prune -o -type f -print0 | cpio -pmd0 --quiet $out_dir/$library
			popd > /dev/null
			echo "android.library.reference.${dependency_index}=../$library/" >> $out_dir/project/project.properties
			dependency_index=$(($dependency_index+1))
		fi
		if [ -d $host_source/external/$library/lib ]; then
#			rsync -r --exclude=.svn --exclude=.DS_Store "$host_source/external/$library/lib/" "$out_dir/project/libs"
			chmod 777 -R $host_source/external/$library/lib/*
			pushd $host_source/external/$library/lib > /dev/null
				find . -name ".?*" -type d -prune -o -type f -print0 | cpio -pmd0 --quiet $out_dir/project/libs
			popd > /dev/null
		fi
		if [ -d $host_source/external/$library/src ]; then
#			rsync -r --exclude=.svn --exclude=.DS_Store "$host_source/external/$library/src/" "$out_dir/project/src"
			chmod 777 -R $host_source/external/$library/src/*
			pushd $host_source/external/$library/src > /dev/null
				find . -name ".?*" -type d -prune -o -type f -print0 | cpio -pmd0 --quiet $out_dir/project/src
			popd > /dev/null
		fi
		if [ -d $host_source/external/$library/res ]; then
#			rsync -r --exclude=.svn --exclude=.DS_Store "$host_source/external/$library/res/" "$out_dir/project/res"
			chmod 777 -R $host_source/external/$library/res/*
			pushd $host_source/external/$library/res > /dev/null
				find . -name ".?*" -type d -prune -o -type f -print0 | cpio -pmd0 --quiet $out_dir/project/res
			popd > /dev/null
		fi
		if [ -d $host_source/external/$library/assets ]; then
#			rsync -r --exclude=.svn --exclude=.DS_Store "$host_source/external/$library/assets/" "$out_dir/project/assets"
			#chmod 777 -R $host_source/external/$library/assets/*
			pushd $host_source/external/$library/assets > /dev/null
				find . -name ".?*" -type d -prune -o -type f -print0 | cpio -pmd0 --quiet $out_dir/project/assets
			popd > /dev/null
		fi
		
		if [ -d assets/$library ]; then
#			rsync -r --exclude=.svn --exclude=.DS_Store "$host_source/external/$library/assets/" "$out_dir/project/assets"
			pushd assets/$library > /dev/null
				find . -name ".?*" -type d -prune -o -type f -print0 | cpio -pmd0 --quiet $out_dir/project/assets
			popd > /dev/null
		fi
	done
	
	fr $out_dir/project/AndroidManifest.xml	@PACKAGE@ "$package"

	# CDSC_TAG_NATIVE_KEYBOARD 哦，宏替换～～
	fr $out_dir/project/res/layout/main.xml	@PACKAGE@ "$package"
	fr $out_dir/project/AndroidManifest.xml	@SCREEN_ORIENTATION@ "$screenOrientation"
	cp777 -f $host_source/project/local.properties $out_dir/project/local.properties
	for file in `find $out_dir/ -name "local.properties"` ; do fr $file @SDK_ROOT@ "$android_sdk_root" ; done

	cp -rf $host_source/project/src $out_dir/project/
	chmod 777 -R $host_source/project/src/*

#	fr $out_dir/project/$package_path/MoaiActivity.java @WORKING_DIR@ "$working_dir"
	
	java_files=`find $out_dir/project/src/ -name "*.java"`	
	for file in $java_files ; do fr $file @WORKING_DIR@ "$working_dir" ; done

#CDSC start	
	for file in $java_files ; do fr $file @ENABLE_ACCELEROMETER@ "$enable_accelerometer" ; done
	for file in $java_files ; do fr $file @APP_PACKAGE@ "$app_package" ; done
	for file in $java_files ; do fr $file @CHANNEL_NAME@ "$channel_name" ; done


#	fr $out_dir/project/src/com/ziplinegames/moai/Moai.java	@CHANNEL_NAME@ "$channel_name"

	if [ x"$channel_name" == xxianguo ]; then
		fr $out_dir/project/src/com/ziplinegames/moai/MoaiXianguo.java	@CHANNEL_ID@ "$sub_channel_id"
	fi
#CDSC end
	working_dir_depth=`grep -o "\/" <<<"$working_dir" | wc -l`
	(( working_dir_depth += 1 ))
	
	init_dir=\.
    if [ x$working_dir != x"." ]; then
        for (( i=1; i<=$working_dir_depth; i++ )); do
            if [ $i == 1 ]; then
                init_dir=\.\.
            else
                init_dir=$init_dir\/\.\.
            fi
        done
    fi
	
	run_command="\"$init_dir/init.lua\""
	
	for file in "${run[@]}"; do
		run_command="$run_command, \"$file\""
	done
	
	run_command="runScripts ( new String [] { $run_command } );"
	java_files=`find $out_dir/project/src/ -name "*.java"`	
	for file in $java_files ; do fr $file @RUN_COMMAND@ "$run_command" ; done
	for file in $java_files ; do fr $file @PACKAGE@ "$package" ; done
	
	cp777 -f $host_source/init.lua $out_dir/project/assets/init.lua
	
	# cdsc add start
	fr $out_dir/project/assets/init.lua	@CHANNEL_NAME@ "$channel_name"
	# cdsc add end
	
	for (( i=0; i<${#src_dirs[@]}; i++ )); do
#		rsync -r --exclude=.svn --exclude=.DS_Store --exclude=*.bat --exclude=*.sh ${src_dirs[$i]}/. $out_dir/project/assets/${dest_dirs[$i]}
		source_dir=${src_dirs[$i]}
		if [ x"$source_dir" != x ] && [ x"$local_root" != x ] && [[ ! $source_dir == /* ]]; then
			source_dir=$local_root/$source_dir
		fi
		pushd $source_dir > /dev/null
			find -L . -name ".?*" -type d -prune -o -name "*.sh" -type f -prune -o -name "*.bat" -type f -prune -o -type f -print0 | cpio --unconditional -pmd0 --quiet $out_dir/project/assets/${dest_dirs[$i]}
			chmod 777 -R $out_dir/project/assets/${dest_dirs[$i]}/*
		popd > /dev/null
	done
	if [ "$debug" == "true" ]; then
		install_cmd="ant debug install"
	else
		install_cmd="ant release install"		
	fi
	
	if [ $OSTYPE != cygwin ]; then
		pushd $out_dir/project > /dev/null
			ant uninstall
			ant clean
			$install_cmd
			adb shell am start -a android.intent.action.MAIN -n $package/$package.MoaiActivity
			adb logcat -c
			adb logcat MoaiLog:V AndroidRuntime:E *:S
		popd > /dev/null
	fi
	
	if [ "$debug" == "true" ]; then
		exit 0
	else
		exit 2
	fi
	
	