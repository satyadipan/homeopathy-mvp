import streamlit as st
import requests

BACKEND_URL = "http://127.0.0.1:8000"  # update to Azure URL later

st.set_page_config(page_title="Homeopathy Doctor Dashboard", layout="wide")
st.title("👨‍⚕️ Doctor Dashboard")

# Fetch all "cases" currently in memory
def get_pending_cases():
    try:
        res = requests.get(f"{BACKEND_URL}/api/case/all")  # we'll add /all route soon
        if res.status_code == 200:
            return res.json()
    except Exception as e:
        st.error(f"Error connecting to backend: {e}")
    return []

# Layout
if st.button("🔄 Refresh Cases"):
    st.session_state.cases = get_pending_cases()

if "cases" not in st.session_state:
    st.session_state.cases = get_pending_cases()

for case in st.session_state.cases:
    st.subheader(f"Case ID: {case['case_id']}")
    st.write(f"**User:** {case['user_id']}")
    st.write(f"**Symptoms:** {case['text']}")
    st.json(case["suggested"])
    st.write("---")
