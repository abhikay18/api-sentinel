from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from model import AnomalyModel
import traceback

app = FastAPI()
model = AnomalyModel()

class PredictRequest(BaseModel):
    requestsPerMinute: int
    errorCount: int
    uniqueEndpoints: int
    avgRequestIntervalMs: float

@app.post("/predict")
def predict(data: PredictRequest):
    try:
        print("REQUEST RECEIVED:", data)

        features = [
            data.requestsPerMinute,
            data.errorCount,
            data.uniqueEndpoints,
            data.avgRequestIntervalMs
        ]

        print("FEATURE VECTOR:", features)

        anomaly, score = model.predict(features)

        print("PREDICTION:", anomaly, score)

        return {
            "anomaly": anomaly,
            "score": score
        }

    except Exception as e:
        print("🔥 ERROR OCCURRED 🔥")
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/health")
def health():
    return {"status": "UP"}
