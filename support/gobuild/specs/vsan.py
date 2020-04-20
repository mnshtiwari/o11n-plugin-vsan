# Copyright 2013 VMware, Inc.  All rights reserved. -- VMware Confidential

VCOSERVER_NG_BRANCH = 'maint-7.3.0'
VCOSERVER_NG_CLN = '049a33220b86cd987029c5136187079706f9c7c0'
VCOSERVER_NG_BUILDTYPE = 'release'
vcoFiles = [r'publish/m2-repo.zip', r'publish/.*vmokeystore']
VCOSERVER_NG_FILES = {
    'linux64': vcoFiles,
    'linux': vcoFiles,
    'windows': vcoFiles,
    'windows-2008': vcoFiles,
}

cisFiles = [ r'publish/.*jar',  r'publish/install-cm.zip',]
CIS_FILES = {
    'linux64': cisFiles,
    'linux': cisFiles,
    'windows': cisFiles,
    'windows-2008': cisFiles,
}
CIS_BRANCH = 'vmkernel-main'
CIS_CLN = 4095730

VLSI_BRANCH = 'main'
VLSI_BUILDTYPE = 'release'
VLSI_CLN = 7684368
VLSI_HOSTTYPES = {
    'linux': 'linux-centos72-gc32-fw',
    'linux64': 'linux-centos72-gc32-fw',
}

RDIDENTITYSERVER_BRANCH = 'vmkernel-main'
RDIDENTITYSERVER_CLN = 3926770
RDIDENTITYSERVER_HOSTTYPES = {
    'linux': 'linux64',
    'linux64': 'linux64',
    'windows': 'windows-2008',
    'windows-2008': 'windows-2008',
}
