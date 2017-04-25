/*
 * Copyright (c) 2017. Hans-Peter Grahsl (grahslhp@gmail.com)
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

package at.grahsl.kafka.connect.mongodb.processor;

import at.grahsl.kafka.connect.mongodb.MongoDbSinkConnectorConfig;
import at.grahsl.kafka.connect.mongodb.converter.SinkDocument;
import at.grahsl.kafka.connect.mongodb.processor.field.projection.BlacklistProjector;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Set;
import java.util.function.Predicate;

public class BlacklistValueProjector extends BlacklistProjector {

    private Predicate<MongoDbSinkConnectorConfig> predicate;

    public BlacklistValueProjector(MongoDbSinkConnectorConfig config) {
        this(config,config.getValueProjectionList(),
                cfg -> cfg.isUsingBlacklistValueProjection());
    }

    public BlacklistValueProjector(MongoDbSinkConnectorConfig config, Set<String> fields,
                                    Predicate<MongoDbSinkConnectorConfig> predicate) {
        super(config);
        this.fields = fields;
        this.predicate = predicate;
    }

    @Override
    public void process(SinkDocument doc, SinkRecord orig) {

        if(predicate.test(getConfig())) {
            doc.getValueDoc().ifPresent(vd ->
                    fields.forEach(f -> doProjection(f,vd))
            );
        }

        getNext().ifPresent(pp -> pp.process(doc,orig));
    }

}
