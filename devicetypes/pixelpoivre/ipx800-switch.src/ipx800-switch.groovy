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
preferences {
	input("ipxAddress","string",title: "IP of IPX800 controller", description: "", defaultValue: "192.168.2.4",
		      required: true, displayDuringSetup: true)
	input("ipxPort","string",title: "Port of IPX800 controller", description: "", defaultValue: "80",
		      required: true, displayDuringSetup: true)
	input("ipxRelay","number", range: "1..8", title: "Relay ID", description: "", defaultValue: "1",
		      required: true, displayDuringSetup: true)
	}
	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}
		main "switch"
		details "switch"
	}
}

def installed() {
}

def parse(String description) {
}

def on() {
    log.trace "on() treated as open()"
	def path = "/user/api.cgi?SetR={$ipxRelay.Left("0",2)}"
	log.debug path
    	def result = new physicalgraph.device.HubAction(
    		method: "GET",
    		path: path,
    		headers: [HOST:getHostAddress()])

    	log.debug result
    	return result
}

def off() {
    log.trace "off() treated as close()"
    def path = "/user/api.cgi?ClearR=$ipxRelay"

    	def result = new physicalgraph.device.HubAction(
    		method: "GET",
    		path: path,
    		headers: [HOST:getHostAddress()])

    	log.debug result
    	return result
}
