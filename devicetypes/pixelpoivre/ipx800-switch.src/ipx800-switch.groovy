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
	tiles(scale: 2) {
        multiAttributeTile(name:"shade", type: "lighting", width: 6, height: 4) {
            tileAttribute("device.windowShade", key: "PRIMARY_CONTROL") {
                attributeState("unknown", label:'${name}', action:"refresh.refresh", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e")
                attributeState("closed",  label:'${name}', action:"open", icon:"st.doors.garage.garage-closed", backgroundColor:"#bbbbdd", nextState: "opening")
                attributeState("open",    label:'up', action:"close", icon:"st.doors.garage.garage-open", backgroundColor:"#ffcc33", nextState: "closing")
                attributeState("partially open", label:'preset', action:"presetPosition", icon:"st.Transportation.transportation13", backgroundColor:"#ffcc33")
                attributeState("closing", label:'${name}', action:"presetPosition", icon:"st.doors.garage.garage-closing", backgroundColor:"#bbbbdd")
                attributeState("opening", label:'${name}', action:"presetPosition", icon:"st.doors.garage.garage-opening", backgroundColor:"#ffcc33")
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState("level", action:"switch level.setLevel")
            }
            tileAttribute ("device.speedLevel", key: "VALUE_CONTROL") {
                attributeState("level", action: "levelOpenClose")
            }
        }

        standardTile("switchmain", "device.windowShade") {
            state("unknown", label:'${name}', action:"refresh.refresh", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e")
            state("closed",  label:'${name}', action:"open", icon:"st.doors.garage.garage-closed", backgroundColor:"#bbbbdd", nextState: "opening")
            state("open",    label:'up', action:"close", icon:"st.doors.garage.garage-open", backgroundColor:"#ffcc33", nextState: "closing")
            state("partially open", label:'preset', action:"presetPosition", icon:"st.Transportation.transportation13", backgroundColor:"#ffcc33")
            state("closing", label:'${name}', action:"presetPosition", icon:"st.doors.garage.garage-closing", backgroundColor:"#bbbbdd")
            state("opening", label:'${name}', action:"presetPosition", icon:"st.doors.garage.garage-opening", backgroundColor:"#ffcc33")

//            state("on", label:'up', action:"switch.off", icon:"st.doors.garage.garage-open", backgroundColor:"#ffcc33")
//            state("off", label:'closed', action:"switch.on", icon:"st.doors.garage.garage-closed", backgroundColor:"#bbbbdd")
//            state("default", label:'preset', action:"presetPosition", icon:"st.Transportation.transportation13", backgroundColor:"#ffcc33")
        }

        standardTile("on", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("on", label:'open', action:"switch.on", icon:"st.doors.garage.garage-opening")
        }
        standardTile("off", "device.stopStr", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("close/stop", label:'close/stop', action:"switch.off", icon:"st.doors.garage.garage-closing")
            state("default", label:'close', action:"switch.off", icon:"st.doors.garage.garage-closing")
        }
        standardTile("preset", "device.stopStr", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("close/stop", label:'slats open', action:"switch level.setLevel", icon:"st.Transportation.transportation13")
            state("default", label:'preset/stop', action:"switch level.setLevel", icon:"st.Transportation.transportation13")
        }
        controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
            state("level", action:"switch level.setLevel")
        }

        standardTile("refresh", "command.refresh", width:2, height:2, inactiveLabel: false, decoration: "flat") {
                state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }


     standardTile("poll", "command.poll", width:2, height:2, inactiveLabel: false, decoration: "flat") {
             state "default", label:'poll', action:"poll", icon:"st.secondary.poll"
     }

        main(["switchmain"])
        details(["shade", "on", "off", "preset"])
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
	def path = "/user/api.cgi?SetR=$ipxRelay"

    	def result = new physicalgraph.device.HubAction(
    		method: "GET",
    		path: path,
    		headers: [HOST:getHostAddress()])

    	log.debug result
    	return result
}
