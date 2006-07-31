<project name="Common targets for inclusion in build files.">
	<patternset id="src.patternset" />
	<patternset id="war.patternset" />

    <path id="classpath.local">
        <fileset dir="${project.dir}" includes="lib/*.jar" />
    </path>

    <target name="javadoc">
        <ant target="javadoc" antfile="${carrot2.cvs.dir}/components/master.xml" inheritall="true" inheritRefs="true" />
    </target>

    <target name="clean">
        <ant target="clean" antfile="${carrot2.cvs.dir}/components/master.xml" inheritall="true" />
    </target>

    <target name="cleanall">
        <ant target="cleanall" antfile="${carrot2.cvs.dir}/components/master.xml" inheritall="true" />
    </target>
    
    <target name="_tests.run">
    	<property name="profile" value="test" />
        <ant target="build.testtype"
             antfile="${carrot2.cvs.dir}/components/master.xml" inheritall="true" inheritrefs="true">
        </ant>
    </target>

    <target name="test">
	    <taskdef resource="net/sf/antcontrib/antlib.xml">
    	    <classpath location="${carrot2.cvs.dir}/lib/ant-extensions/ant-contrib.jar" />
  	    </taskdef>
    	
    	<if>
    		<equals arg1="true" arg2="${tests.available}" casesensitive="false" trim="true" />
    		<then>
		    	<if>
    				<isset property="cache:deps" />
    				<then>
				    	<antcall target="_tests.run">
				            <reference refid="dependencies" />
				            <reference refid="c2.tasks.classloader" />
				            <param name="cache:deps" value="${cache:deps}" />
				        </antcall>
    				</then>
    				<else>
				    	<antcall target="_tests.run" />
    				</else>
    			</if>
    		</then>
    		<else>
		    	<echo message="This component has no tests." />
    		</else>
    	</if>
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