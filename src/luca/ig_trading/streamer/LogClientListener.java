/*
 * Copyright (c) Lightstreamer Srl
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package luca.ig_trading.streamer;

import com.lightstreamer.client.LightstreamerClient;
import luca.ig_trading.Logger.ClientErrorListener;
import org.pmw.tinylog.Logger;

public class LogClientListener implements com.lightstreamer.client.ClientListener {

    private ClientErrorListener clientErrorListener;

    public LogClientListener(ClientErrorListener clientErrorListener) {
        this.clientErrorListener = clientErrorListener;
    }

    @Override
    public void onListenEnd(LightstreamerClient client) {
      Logger.info("Stops listening to client: " + client.connectionDetails.toString());
    }

    @Override
    public void onListenStart(LightstreamerClient client) {
      Logger.info("Start listening to client: " + client.connectionDetails.toString() );
    }

    @Override
    public void onPropertyChange(String property) {
      Logger.info("Client property changed: " + property);
    }

    @Override
    public void onServerError(int code, String message) {
      Logger.info("Server error: " + code + ": " + message);
      clientErrorListener.onError(code);
    }

    @Override
    public void onStatusChange(String newStatus) {
      Logger.info("Connection status changed to " + newStatus);
    }

  
  
}
