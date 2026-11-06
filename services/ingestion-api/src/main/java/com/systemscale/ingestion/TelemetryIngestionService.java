package com.systemscale.ingestion;

import com.systemscale.ingestion.grpc.IngestionServiceGrpc;
import com.systemscale.ingestion.grpc.TelemetryEventRequest;
import com.systemscale.ingestion.grpc.IngestionResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@GrpcService
public class TelemetryIngestionService extends IngestionServiceGrpc.IngestionServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(TelemetryIngestionService.class);

    @Override
    public void routeTelemetryEvent(TelemetryEventRequest request, StreamObserver<IngestionResponse> responseObserver) {
        // System Design Pillar: Capture metrics on incoming thread execution profiles
        log.info("Processing ingestion payload on thread: {} | Device ID: {}", 
                Thread.currentThread(), request.getDeviceId());

        try {
            // Week 3/4 Hook: This is where we will write validation parameters 
            // to check the request.getIdempotencyKey() against our Redis cluster.
            
            // Build out our operational system response metadata
            IngestionResponse response = IngestionResponse.newBuilder()
                    .setStatus(IngestionResponse.StatusCode.SUCCESS)
                    .setTrackingId(UUID.randomUUID().toString())
                    .setMessage("Payload accepted successfully onto ingest pipe.")
                    .build();

            // Emit the response back over the HTTP/2 connection channel
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Fatal failure on event ingestion loop", e);
            
            IngestionResponse errorResponse = IngestionResponse.newBuilder()
                    .setStatus(IngestionResponse.StatusCode.INTERNAL_FAILURE)
                    .setMessage("Cluster node failure during partition write: " + e.getMessage())
                    .build();
            
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
}
