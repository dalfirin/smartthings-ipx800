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
	definition (name: "IPX800 Window Shade", namespace: "pixelpoivre", author: "piXelPoivre") {
        capability "Switch Level"
        capability "Switch"
        capability "Window Shade"
        //capability "Polling"
        capability "Refresh"
        capability "Actuator"
        capability "Health Check"

        attribute "stopStr", "enum", ["preset/stop", "close/stop"]

        command "OpenSync"
        command "CloseSync"
        command "TiltSync"
        command "levelOpenClose"

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
		input("ipxV4RController","number", range: "1..8", title: "Controller ID", description: "", defaultValue: "1",
		      required: true, displayDuringSetup: true)
 input("ipxShadeID","number", range: "1..4", title: "Window shade ID", description: "", defaultValue: "1",
		      required: true, displayDuringSetup: true)
	input ("shadeType", "enum", options:[
		"shades": "Window Shades",
		"blinds": "Window Blinds"],
		title: "Window Shades or Blinds?", description: "set type (shades or blinds)", defaultValue: "shades",
                required: false, displayDuringSetup: true )
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

//  Poll provides data, but the ZRTSII does not provide accurate status
//
//      standardTile("poll", "command.poll", width:2, height:2, inactiveLabel: false, decoration: "flat") {
//              state "default", label:'poll', action:"poll", icon:"st.secondary.poll"
//      }

        main(["switchmain"])
        details(["shade", "on", "off", "preset"])
    }
}

def configure() {
    log.trace "configure() called"
    updated()
}

def ping() {
	refresh()
}

def updated() {
    log.trace "updated() called"

	def result = new physicalgraph.device.HubAction(
    method: "GET",
    path: "/api/xdevices.json",
    headers: [
        HOST: "$ipxAddress"
    ],
    query: [Get: "VR$(ipxV4RController)"]
)
	
		
	
    def currstat = device.latestValue("level")
    def currstat1 = device.latestValue("windowShade")

    log.debug "Shade type: ${settings?.shadeType}"
    if (settings?.shadeType) {
        if (settings.shadeType == "shades") {
            sendEvent(name: "stopStr", value: "preset/stop")
        } else {
            sendEvent(name: "stopStr", value: "close/stop")
        }
    } else {
        sendEvent(name: "stopStr", value: "preset/stop")
    }

    log.debug "switch state: ${currstat}  windowShade state: ${currstat1}"
    if ( (currstat == null) || (currstat1 == null)) {
        if (currstat > null) {
            if (currstat >= 75) {
                //sendEvent(name: "windowShade", value: "open")
                finishOpenShade()
            } else if (currstat <= 25) {
                //sendEvent(name: "windowShade", value: "closed")
                finishCloseShade()
            } else {
                //sendEvent(name: "windowShade", value: "partially open")
                finishPartialOpenShade()
            }
        }
    }
}

def parse(String description) {
    description
    def result = null
    def cmd = zwave.parse(description, [0x20: 1, 0x26: 1, 0x70: 1])
    log.debug "Parsed ${description} to ${cmd}"
    if (cmd) {
        result = zwaveEvent(cmd)
        log.debug "zwaveEvent( ${cmd} ) returned ${result.inspect()}"
    } else {
        log.debug "Non-parsed event: ${description}"
    }
    return result
}

def levelOpenClose(value) {
    log.trace "levelOpenClose called with value $value"
    if (value) {
        on()
    } else {
        off()
    }
}

private setDeviceId() {
	//def userpassascii = "${ipxUser}:${ipxPassword}"
	//def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def host = ipxAddress 
    def hosthex = convertIPtoHex(host)
    //def porthex = convertPortToHex(CameraPort)
    device.deviceNetworkId = "$hosthex"//:$porthex" 
    
    log.debug "The device ID is: $device.deviceNetworkId"	
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    log.debug "IP address entered is $ipAddress and the converted hex code is $hex"
    return hex

}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    log.debug hexport
    return hexport
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}


private String convertHexToIP(hex) {
	log.debug("Convert hex to ip: $hex") 
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {
    def parts = device.deviceNetworkId.split(":")
    log.debug device.deviceNetworkId
    def ip = convertHexToIP(parts[0])
    //def port = convertHexToInt(parts[1])
    return ip //+ ":" + port
}

def on() {
    int level = 100
    log.trace "on() treated as open()"
    setLevel(level) 
}

def off() {
    int level = 0
    log.trace "off() treated as close()"
    setLevel(level) 
}

def setLevel() {
    log.trace "setLevel() treated as preset position"
    setLevel(50) 
}

def open() {
    log.trace "open()"
    on()
}

def close() {
    log.trace "close()"
    off()
}

def presetPosition() {
    log.trace "presetPosition()"
    setLevel(50)
}

def OpenSync() {
    log.trace "OpenSync()"
    finishOpenShade()
}

def CloseSync() {
    log.trace "CloseSync()"
    finishCloseShade()
}

def TiltSync() {
    log.trace "TiltSync()"
    finishPartialOpenShade()
}

def refresh() {
    log.trace "refresh()"
    delayBetween([
        //zwave.switchBinaryV1.switchBinaryGet().format(),
        //zwave.switchMultilevelV1.switchMultilevelGet().format(),
        //zwave.meterV2.meterGet(scale: 0).format(),      // get kWh
        //zwave.meterV2.meterGet(scale: 2).format(),      // get Watts
        //zwave.sensorMultilevelV1.sensorMultilevelGet().format(),
        //zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:1, scale:1).format(),  // get temp in Fahrenheit
        //zwave.batteryV1.batteryGet().format(),
        zwave.basicV1.basicGet().format()
    ], 3000)
}

// If you add the Polling capability to your device type, this command
// will be called approximately every 5 minutes to check the device's state
// zrtsII does not provide accurate status of shade position

//def poll() {
//        log.trace "Poll"
//        zwave.basicV1.basicGet().format()
//}

def setLevel(level) {
    log.trace "setLevel(level)  {$level}"
    log.debug "level.inspect " + level.inspect()

    int newlevel = level

    if (level > null) {

        if (level >= 75) {
            sendEvent(name: "windowShade", value: "opening")
            sendEvent(name: "level", value: level)
            sendEvent(name: "switch", value: "on")
            runIn(25, "finishOpenShade", [overwrite: true])
            delayBetween([
                zwave.switchMultilevelV1.switchMultilevelSet(value: 0xFF).format(),
                zwave.basicV1.basicGet().format()
//                sendEvent(name: "windowShade", value: "open"),
//                sendEvent(name: "switch", value: "on")
            ], 4000)
        } else if (level <= 25) {
            sendEvent(name: "windowShade", value: "closing")
            sendEvent(name: "switch", value: "off")
            runIn(25, "finishCloseShade", [overwrite: true])
            if (settings.shadeType == "shades") {
                delayBetween([
                    zwave.switchMultilevelV1.switchMultilevelSet(value: 0x00).format(),
                    zwave.basicV1.basicGet().format()
//                    sendEvent(name: "windowShade", value: "closed"),
//                    sendEvent(name: "switch", value: "off")
                ], 4000)
            } else {
                delayBetween([
                    zwave.switchMultilevelV1.switchMultilevelStopLevelChange().format(),
                    zwave.basicV1.basicGet().format()
//                    sendEvent(name: "windowShade", value: "closed"),
//                    sendEvent(name: "switch", value: "off")
                ], 4000)
            }
        } else {
            def currstat = device.latestValue("windowShade")
            if (currstat == "open") { sendEvent(name: "windowShade", value: "closing") }
            else { sendEvent(name: "windowShade", value: "opening") }
            sendEvent(name: "level", value: level)
            sendEvent(name: "switch", value: "on")
            runIn(15, "finishPartialOpenShade", [overwrite: true])
            if (settings.shadeType == "shades") {
                delayBetween([
                    zwave.switchMultilevelV1.switchMultilevelStopLevelChange().format(),
                    zwave.basicV1.basicGet().format()
//                    sendEvent(name: "windowShade", value: "partially open"),
//                    sendEvent(name: "switch", value: "default")
                ], 4000)
            } else {
                delayBetween([
                    zwave.switchMultilevelV1.switchMultilevelSet(value: 0x00).format(),
                    zwave.basicV1.basicGet().format()
//                    sendEvent(name: "windowShade", value: "partially open"),
//                    sendEvent(name: "switch", value: "default")
                ], 4000)
            }
        }

        // this code below causes commands not be sent/received by the Somfy ZRTSII - I assume delayBetween is asynchronous...

        //log.trace("finished level adjust")
        //if (newlevel != level) { 
            //log.trace("finished level adjust1")
            //delayBetween([
                //sendEvent(name: "level", value: newlevel)
            //], 1000)
        //}
    }
}

def finishOpenShade() {
    sendEvent(name: "windowShade", value: "open")
    def newlevel = 100
    sendEvent(name: "level", value: newlevel)
    sendEvent(name: "switch", value: "on")
}

def finishCloseShade() {
    sendEvent(name: "windowShade", value: "closed")
    def newlevel = 100
    sendEvent(name: "level", value: newlevel)
    sendEvent(name: "switch", value: "off")
}

def finishPartialOpenShade() {
    sendEvent(name: "windowShade", value: "partially open")
    def newlevel = 50
    sendEvent(name: "level", value: newlevel)
    sendEvent(name: "switch", value: "on")
}

// this appears to never be called

//def setLevel(level, duration) {
//    log.trace "setLevel(level, duration)  {$level} ${duration}"
//    setLevel(level)
//    return
//}
