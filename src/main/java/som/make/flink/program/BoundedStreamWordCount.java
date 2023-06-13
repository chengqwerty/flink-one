package som.make.flink.program;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class BoundedStreamWordCount {

    public static void main(String[] args) throws Exception {

        // 创建执行环境
        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        FileSource<String> fileSource = FileSource.forRecordStreamFormat(new TextLineInputFormat(), new Path("input/hello")).build();

        // 获取数据源
        DataStreamSource<String> dataStreamSource = streamExecutionEnvironment.fromSource(fileSource, WatermarkStrategy.noWatermarks(), "file" +
                "-source");

        // 转换
        SingleOutputStreamOperator<Tuple2<String, Long>> tuple2SingleOutputStreamOperator = dataStreamSource.flatMap((String line, Collector<Tuple2<String, Long>> out) -> {
            String[] words = line.split(" ");
            for (String word : words) {
                out.collect(Tuple2.of(word, 1L));
            }
        });

        // 分组
        KeyedStream<Tuple2<String, Long>, String> tuple2StringKeyedStream = tuple2SingleOutputStreamOperator.keyBy(data -> data.f0);

        // 求和
        SingleOutputStreamOperator<Tuple2<String, Long>> sum = tuple2StringKeyedStream.sum(1);

        // 打印
        sum.print();

        // 启动执行
        streamExecutionEnvironment.execute();
    }

}
