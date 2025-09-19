import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, accuracy_score
import joblib
import os

DATA_PATH = "data/IMDB Dataset.csv"
MODELS_DIR = "models"

os.makedirs(MODELS_DIR, exist_ok=True)

def load_data(path=DATA_PATH):
    df = pd.read_csv(path)
    df['sentiment'] = df['sentiment'].map(lambda s: 1 if s.lower() == "positive" else 0)
    return df

def train():
    df = load_data()
    x = df['review'].astype(str)
    y = df['sentiment']

    vectorizer = TfidfVectorizer(max_features=20000, ngram_range=(1,2))
    x_vec = vectorizer.fit_transform(x)

    x_train, x_test, y_train, y_test = train_test_split(x_vec, y, test_size=0.2, random_state=42)

    model = LogisticRegression(max_iter=1000)
    model.fit(x_train, y_train)

    y_pred = model.predict(x_test)
    print("Accuracy:", accuracy_score(y_test, y_pred))
    print(classification_report(y_test, y_pred))

    joblib.dump(model, os.path.join(MODELS_DIR, "sentiment_model.pkl"))
    joblib.dump(vectorizer, os.path.join(MODELS_DIR, "vectorizer.pkl"))
    print("Models saved to", MODELS_DIR)


if __name__ == "__main__":
    train()
