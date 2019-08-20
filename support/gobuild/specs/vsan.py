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

VIMBASE_BRANCH = 'vsphere67u2'
VIMBASE_CLN = 6839609
VIMBASE_BUILDTYPE = 'release'
VIMBASE_HOSTTYPES = {
    'linux': 'linux64',
    'linux64': 'linux64',
    'windows': 'windows-2016-vs2013-U5',
    'windows-2008': 'windows-2016-vs2013-U5',
}

esxVsanhealthFiles = [r'publish/vsanmgmt-public/java/*',]
ESX_VSANHEALTH_FILES = {
    'linux64': esxVsanhealthFiles,
    'linux': esxVsanhealthFiles,
    'windows': esxVsanhealthFiles,
    'windows-2008': esxVsanhealthFiles,
}
ESX_VSANHEALTH_BRANCH = 'vsan67-patch-sdk'
ESX_VSANHEALTH_CLN = 6443945
ESX_VSANHEALTH_BUILDTYPE = 'release'

VLSI_BRANCH = 'vsphere67u2'
VLSI_BUILDTYPE = 'release'
VLSI_CLN = 6839609
VLSI_HOSTTYPES = {
    'linux': 'linux64',
    'linux64': 'linux64',
    'windows': 'windows-2008',
    'windows-2008': 'windows-2008',
}

RDIDENTITYSERVER_BRANCH = 'vmkernel-main'
RDIDENTITYSERVER_CLN = 3926770
RDIDENTITYSERVER_HOSTTYPES = {
    'linux': 'linux64',
    'linux64': 'linux64',
    'windows': 'windows-2008',
    'windows-2008': 'windows-2008',
}
