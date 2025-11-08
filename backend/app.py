from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import csv
import uuid
from difflib import SequenceMatcher
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(title="Homeopathy AI Backend")

# Allow Android app & dashboard to access backend (CORS)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # in production, replace * with your app domains
    allow_methods=["*"],
    allow_headers=["*"],
)

# CSV dataset path
DATA_CSV = "data/remedies.csv"

# Temporary in-memory store for session records (you can replace with DB later)
cases = {}

# Input structure
class SymptomRequest(BaseModel):
    user_id: str
    text: str

@app.post("/api/symptom")
def get_suggestion(data: SymptomRequest):
    """
    Accept user symptom text and return best-matching remedies.
    """
    best_matches = match_remedy(data.text)

    case_id = str(uuid.uuid4())
    cases[case_id] = {
        "case_id": case_id,
        "user_id": data.user_id,
        "text": data.text,
        "suggested": best_matches,
        "status": "pending"
    }

    return {
        "case_id": case_id,
        "suggested": best_matches
    }


@app.get("/api/case/all")
def get_all_cases():
    """Return all cases in memory (for dashboard preview)."""
    return list(cases.values())

@app.get("/api/case/{case_id}")
def get_case(case_id: str):
    """
    Fetch a previously created case by ID.
    Useful for history or doctor dashboard.
    """
    if case_id not in cases:
        raise HTTPException(status_code=404, detail="Case not found")
    return cases[case_id]


def match_remedy(symptom_text):
    """
    Basic fuzzy match between user symptom and dataset keywords.
    """
    remedies = []
    with open(DATA_CSV, newline='', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            score = SequenceMatcher(None, symptom_text.lower(), row["symptom_keywords"].lower()).ratio()
            remedies.append((score, row))
    remedies.sort(reverse=True, key=lambda x: x[0])
    top = remedies[:2]

    return [
        {
            "name": r["remedy"],
            "potency": r["potency"],
            "dosage": r["dosage"],
            "rationale": r["rationale"],
            "confidence": round(s * 100, 2)
        }
        for s, r in top
    ]
