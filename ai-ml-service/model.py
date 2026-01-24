from sklearn.ensemble import IsolationForest
import numpy as np

class AnomalyModel:

    def __init__(self):
        self.model = IsolationForest(contamination=0.25, random_state=42)
        self.model.fit([
            [50, 1, 3, 200],
            [60, 0, 2, 180],
            [55, 2, 4, 210],
            [300, 20, 15, 20]  # anomaly
        ])

    def predict(self, features):
        X = np.array([features])
        score = float(self.model.decision_function(X)[0])
        anomaly = bool(self.model.predict(X)[0] == -1 or score < -0.25)
        return anomaly, score
