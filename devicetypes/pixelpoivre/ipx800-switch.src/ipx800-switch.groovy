/**
 *  Copyright 2016 SmartThings, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "IPX800 Switch", namespace: "pixelpoivre", author: "piXelPoivre") {
        capability "Switch"
        //capability "Polling"
        capability "Refresh"
        capability "Actuator"
        capability "Health Check"
    }

    simulator {
        status "on":  "command: 2003, payload: FF"
        status "off": "command: 2003, payload: 00"
        status "09%": "command: 2003, payload: 09"
        status "10%": "command: 2003, payload: 0A"
        status "33%": "command: 2003, payload: 21"
        status "66%": "command: 2003, payload: 42"
        status "99%": "command: 2003, payload: 63"
        
        // reply messages
        reply "2001FF,delay 5000,2602": "command: 2603, payload: FF"
        reply "200100,delay 5000,2602": "command: 2603, payload: 00"
        reply "200119,delay 5000,2602": "command: 2603, payload: 19"
        reply "200132,delay 5000,2602": "command: 2603, payload: 32"
        reply "20014B,delay 5000,2602": "command: 2603, payload: 4B"
        reply "200163,delay 5000,2602": "command: 2603, payload: 63"
    }
		}

		main("rangeValue")
		details([
			"tinySlider", "mediumSlider",
			"largeSlider",
			"rangeSlider", "rangeValue",
			"rangeSliderConstrained"
		])
	}
}

def installed() {
}

def parse(String description) {
}

def on() {
    log.trace "on() treated as open()"
    return setLevel(0) 
}

def off() {
    log.trace "off() treated as close()"
    return setLevel(100) 
}

def defineState(int command) {
	def path = "/user/api.cgi?Set4VR=$ipxV4RController&VrNum=$ipxShadeID&VrPercent=$level"

    	def result = new physicalgraph.device.HubAction(
    		method: "GET",
    		path: path,
    		headers: [HOST:getHostAddress()])

    	log.debug result
    	return result
}
