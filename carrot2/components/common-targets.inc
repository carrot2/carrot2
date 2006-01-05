<project name="Common targets for inclusion in build files.">
	<patternset id="src.patternset" />
    <path id="classpath.local">
        <fileset dir="${project.dir}" includes="lib/*.jar" />
    </path>

    <target name="javadoc">
        <ant target="javadoc" antfile="${carrot2.cvs.dir}/components/master.xml" inheritall="true" 
        	inheritRefs="true" />
    </target>

    <target name="clean">
        <ant target="clean" antfile="${carrot2.cvs.dir}/components/master.xml" inheritall="true" />
    </target>

    <target name="cleanall">
        <ant target="cleanall" antfile="${carrot2.cvs.dir}/components/master.xml" inheritall="true" />
    </target>
    
    <target name="tests.run" if="tests.available.true">
    	<property name="profile" value="test" />
        <ant target="build.testtype"
             antfile="${carrot2.cvs.dir}/components/master.xml" inheritall="true" inheritrefs="true">
        </ant>
    </target>

	<target name="tests.info" unless="tests.available.true">
    	<echo message="This component has no tests." />
	</target>

    <target name="test">
    	<condition property="tests.available.true" value="true">
    		<equals arg1="true" arg2="${tests.available}" casesensitive="false" trim="true" />
    	</condition>
    	<antcall target="tests.run" />
    	<antcall target="tests.info" />
    </target>

    <target name="build">
        <ant target="build.${type}"
             antfile="${carrot2.cvs.dir}/components/master.xml" inheritall="true" inheritrefs="true">
        </ant>
    </target>
	
	<target name="tomcat.deploy">
		<ant target="tomcat.deploy"
			antfile="${carrot2.cvs.dir}/components/master.xml" inheritall="true" inheritrefs="true">
		</ant>
	</target>

	<target name="tomcat.undeploy">
		<ant target="tomcat.undeploy"
			antfile="${carrot2.cvs.dir}/components/master.xml" inheritall="true" inheritrefs="true">
		</ant>
	</target>

	<!-- an alias for dependencies-->
    <target name="deps" depends="dependencies" />
    <target name="dependencies">
        <ant target="dependencies"
             antfile="${carrot2.cvs.dir}/components/master.xml" inheritall="true" inheritrefs="true">
        </ant>
    </target>

</project>