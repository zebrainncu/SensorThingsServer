/*
 * Copyright (C) 2016 Fraunhofer IOSB
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.sta.mqtt;

import de.fraunhofer.iosb.ilt.sta.settings.CoreSettings;
import de.fraunhofer.iosb.ilt.sta.settings.MqttSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jab
 */
public class MqttServerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttManager.class);
    private static final String ERROR_MSG = "Could not generate MqttServer instance: ";
    private static MqttServerFactory instance;

    public static MqttServerFactory getInstance() {
        if (instance == null) {
            instance = new MqttServerFactory();
        }
        return instance;
    }

    private MqttServerFactory() {

    }

    public MqttServer get(CoreSettings settings) {
        MqttSettings mqttSettings = settings.getMqttSettings();
        if (mqttSettings == null || !mqttSettings.isEnableMqtt()) {
            return null;
        }
        MqttServer mqttServer = null;
        try {
            Class<?> clazz = Class.forName(mqttSettings.getMqttServerImplementationClass());
            if (!MqttServer.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("MqttImplementationClass must implement interface '" + MqttServer.class.getName() + "'");
            }
            mqttServer = (MqttServer) clazz.newInstance();
            mqttServer.init(settings);
        } catch (ClassNotFoundException ex) {
            LOGGER.error(ERROR_MSG + "Class '" + mqttSettings.getMqttServerImplementationClass() + "' could not be found", ex);
        } catch (InstantiationException | IllegalAccessException ex) {
            LOGGER.error(ERROR_MSG + "Class '" + mqttSettings.getMqttServerImplementationClass() + "' could not be instantiated", ex);
        }
        return mqttServer;
    }

}
