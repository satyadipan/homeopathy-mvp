# Use an official, small Python base image
FROM python:3.11-slim

# Install system deps sometimes needed for packages (kept minimal)
RUN apt-get update \
  && apt-get install -y --no-install-recommends gcc build-essential libpq-dev \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy only requirements first to leverage Docker cache
COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r /app/requirements.txt

# Copy the rest of the backend source
COPY . /app

# Expose port used by Uvicorn
EXPOSE 8000

# Run the app with uvicorn. Proxy headers helps when running behind a reverse proxy.
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000", "--proxy-headers"]
