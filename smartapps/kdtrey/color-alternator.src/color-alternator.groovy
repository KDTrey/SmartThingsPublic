/**
 *  Color Alternator
 *
 *  Copyright 2016 Dominique Adkins
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
definition(
    name: "Color Alternator",
    namespace: "KDTrey",
    author: "Dominique Adkins",
    description: "Switches the color of a bulb every X minutes",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Using this:") {
		input "master", "capability.colorControl", multiple: false, required: true, title: "Master device"
	}
    section("Control these:") {
    	input "slaves", "capability.colorControl", multiple: true, required: true, title: "Slave devices"
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(master, "switch.on", onHandler)
	subscribe(master, "switch.off", offHandler)
	subscribe(master, "colorRed", setColorHandler1)
    subscribe(master, "colorGreen", setColorHandler2)
}

def onHandler(evt) {
	slaves.on()
    runEvery5Minutes(setColorHandler1)
}

def offHandler(evt) {
	unschedule()//cancels all schedule methods
	slaves.off()
}

def setColorHandler1(evt){
	//sets bulb to green
    slaves.setColor([hue: 120, saturation: 100])
    runIn(60, setColorHandler2)//changes to green in 1 minute
    sendEvent(name: "color1", value: evt, isStateChange: true)
}

def setColorHandler2(evt){
	//sets bulb to red
	slaves.setColor([hue:0, saturation: 100])
    runIn(60, setColorHandler1)//changes to red in 1 minute
    sendEvent(name: "color2", value: evt, isStateChange: true)
}
// TODO: implement event handlers