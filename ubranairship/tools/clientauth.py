#!/usr/bin/env python

import getpass
import urllib
import urllib2


class ClientLoginTokenFactory():
    _token = None

    def __init__(self):
        self.url = 'https://www.google.com/accounts/ClientLogin'
        self.accountType = 'HOSTED_OR_GOOGLE'
        self.email = raw_input("C2DM Google Account: ")
        self.password = getpass.getpass("Password for %s: " % self.email)
        self.source = 'com.urbanairship.c2dm'
        self.service = 'ac2dm'

    def getToken(self):
        if self._token is None:

            # Build payload
            values = {'accountType': self.accountType,
                      'Email': self.email,
                      'Passwd': self.password,
                      'source': self.source,
                      'service': self.service}

            # Build request
            data = urllib.urlencode(values)
            request = urllib2.Request(self.url, data)

            # Post
            response = urllib2.urlopen(request)
            responseAsString = response.read()

            # Format response
            responseAsList = responseAsString.split('\n')
            self._token = responseAsList[2].split('=')[1]
            print "Token:"
            print self._token

        return self._token


ClientLoginTokenFactory().getToken()
