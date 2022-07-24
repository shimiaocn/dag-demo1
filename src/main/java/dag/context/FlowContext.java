package dag.context;

import com.google.common.collect.Maps;
import dag.worker.result.WorkResult;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

public class FlowContext {

    @Getter
    @Setter
    private Object flowGlobalInfo;

    @Getter
    private final String flowId;

    @Getter
    private final Map<String, WorkResult<?>> resultMap = Maps.newHashMap();

    public <R> R getResultByWorkId(String workId) {
        return (R) resultMap.get(workId).getResult();
    }

    public FlowContext() {
        this.flowId = System.currentTimeMillis() + "_" + UUID.randomUUID();
    }

    public FlowContext(String flowId) {
        this.flowId = System.currentTimeMillis() + "_" + flowId;
    }
}
