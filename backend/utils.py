import re

def clean_text(text):
    if not isinstance(text, str):
        return ""
   
    text = text.lower().strip()
    text = re.sub(r'\s+', ' ', text)
    return text

def sentiment_to_label(label_int):
    mapping = {0: "négatif", 1: "positif"}
    return mapping.get(int(label_int), "négatif")

def generate_response_rule(sentiment_label, text):
  
    if sentiment_label == "positif":
        return "Merci beaucoup pour votre retour positif ! Nous sommes heureux que cela vous ait plu."
    else:
        return ("Nous sommes désolés que vous ayez eu une mauvaise expérience. "
                "Votre retour est important pour améliorer nos services.")