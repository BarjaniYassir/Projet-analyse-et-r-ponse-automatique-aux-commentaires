# app.py
from flask import Flask, request, jsonify
from flask_cors import CORS
import joblib
import os
from utils import sentiment_to_label, generate_response_rule

MODELS_DIR = "models"
MODEL_PATH = os.path.join(MODELS_DIR, "sentiment_model.pkl")
VECT_PATH = os.path.join(MODELS_DIR, "vectorizer.pkl")

app = Flask(__name__)
CORS(app)

if not os.path.exists(MODEL_PATH) or not os.path.exists(VECT_PATH):
    raise FileNotFoundError(" Modèle non trouvé. Lance `python train.py` avant.")

model = joblib.load(MODEL_PATH)
vectorizer = joblib.load(VECT_PATH)

@app.route("/analyze", methods=["POST"])
def analyze():
    data = request.get_json(force=True)
    text = data.get("text", "")
    vect = vectorizer.transform([text])
    pred = model.predict(vect)[0]
    label = sentiment_to_label(pred)
    response = generate_response_rule(label, text)
    return jsonify({
        "text": text,
        "sentiment": label,
        "response": response
    })

@app.route("/", methods=["GET"])
def index():
    return " Comment-AI API is running."

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
