# Copyright 2013 VMware, Inc.  All rights reserved. -- VMware Confidential

import helpers.target
import helpers.env
import helpers.maven
import os

import specs.vsan

class vSAN(helpers.target.Target, helpers.maven.MavenHelper):
    """
    vCO vSAN Plug-In
    """

    @staticmethod
    def GetChangeForConsumer(self):
        return '9c5e6d79a186111d5cfe6248ca8ed9d386d1b0f3' # SCM identifier for a change

    def GetBuildProductNames(self):
        return {'name': 'vco-vsan-plugin',
                'longname': 'Orchestrator vSAN Plugin'}

    def GetClusterRequirements(self):
        return ['linux64']

    def GetRepositories(self, hosttype):
        return [{'rcs': 'git', 'src': 'o11nplugin-vsan.git;%(branch);', 'dst': 'src'}]

    def GetCommands(self, hosttype):
        opts = {
            '--errors': None,
            '--batch-mode': None,
            '-s': '%(buildroot)/src/build-resources/maven-settings.xml',
            '--activate-profiles': 'gobuild',
            }

        properties = {
            'maven.repo.local': '%(buildroot)/m2-repository',
            'build.number' : '%(buildnumber)',
            #Fail Sandbox builds if Tests fail.
            #'maven.test.failure.ignore': 'true'
        }

        if str(self.options.get('buildtype')) == 'release':
            properties['allowedMask'] = 'vf'
            properties['installation.mode'] = 'version'

        return [{
            'desc': 'Populating local mvn repo with o11n artifacts',
            'root': '%(buildroot)/src/lib',
            'log': 'm2.log',
            'command': self._Command(hosttype,
                targets='clean install',
                mavenversion='3.3.9',
                mavenoptions=opts,
                **properties),
            'env': self._GetEnvironment(hosttype, 'jdk-1.7.0_121'),
            }, {
            'desc': 'Building Orchestrator vSAN Plugin',
            'root': '%(buildroot)/src',
            'log': 'build.log',
            'command': self._Command(hosttype,
                targets='clean install',
                mavenversion='3.3.9',
                mavenoptions=opts,
                **properties),
            'env': self._GetEnvironment(hosttype, 'jdk-1.8.0_131'),
        }]


    def GetStorageInfo(self, hosttype):
        storinfo = []
        storinfo += [{'type': 'source', 'src': 'src'}]
        storinfo += [{'type': 'build', 'src': 'src/build'}]
        return storinfo

    def GetComponentDependencies(self):
        comps = {}

        comps['vcoserver-ng'] = {
           'branch': specs.vsan.VCOSERVER_NG_BRANCH,
           'change': specs.vsan.VCOSERVER_NG_CLN,
           'files': specs.vsan.VCOSERVER_NG_FILES,
        }

        comps['cis'] = {
           'branch': specs.vsan.CIS_BRANCH,
           'change': specs.vsan.CIS_CLN,
           'files': specs.vsan.CIS_FILES,
        }

        comps['vlsi'] = {
            'branch':    specs.vsan.VLSI_BRANCH,
            'change':    specs.vsan.VLSI_CLN,
            'hosttypes': specs.vsan.VLSI_HOSTTYPES,
            'buildtype': specs.vsan.VLSI_BUILDTYPE,

        }

        comps['rd-identity-server'] = {
            'branch': specs.vsan.RDIDENTITYSERVER_BRANCH,
            'change': specs.vsan.RDIDENTITYSERVER_CLN,
            'hosttypes': specs.vsan.RDIDENTITYSERVER_HOSTTYPES,
        }
        
        return comps

    def GetBuildProductVersion(self, hosttype):
        return "6.7.1"

    def _GetEnvironment(self, hosttype, jdk):
        env = helpers.env.SafeEnvironment(hosttype)
        env['JAVA_HOME'] = '/build/toolchain/lin64/' + jdk
        env['M2_HOME'] = '/build/toolchain/noarch/apache-maven-3.3.9'
        bindirs = [
            '/build/toolchain/lin64/coreutils-8.6/bin',
            '/build/toolchain/lin64/' + jdk + '/bin',
            '/build/toolchain/lin64/sed-4.1.5/bin',
            '/build/toolchain/lin64/grep-2.5.1a/bin',
            '/build/toolchain/lin64/gawk-3.1.5/bin'
            '/build/toolchain/lin64/tar-1.23/bin/',
            '/build/toolchain/lin64/rpm-4.4.2.1/bin/',
            '/build/toolchain/lin64/gzip-1.2.4a/bin/',
            '/build/toolchain/lin64/rpm-4.4.2.1/bin/',
            '/build/toolchain/lin64/diffutils-2.8/bin/',
            '/build/toolchain/lin64/tar-1.23/bin'
        ]
        env['PATH'] = os.pathsep.join(bindirs)
        return env
