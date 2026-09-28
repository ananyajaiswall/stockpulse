# StockPulse — How to Run

## Prerequisites
- Java 17 or higher
- Maven 3.8 or higher
- Node.js 16 or higher
- npm 8 or higher

## Running the Backend

### Option 1: Using the batch script
Double-click on `run-backend.bat` file

### Option 2: Manual steps
1. Open a terminal/command prompt
2. Navigate to the `backend` directory
3. Run: `mvn spring-boot:run`

The backend will start on http://localhost:8080

## Running the Frontend

### Option 1: Using the batch script
Double-click on `run-frontend.bat` file

### Option 2: Manual steps
1. Open a terminal/command prompt
2. Navigate to the `frontend` directory
3. Run: `npm install`
4. Run: `npm run dev`

The frontend will start on http://localhost:5173

## Demo Scenarios

### Low Stock Scenario (PRD-003)
1. Open the frontend at http://localhost:5173
2. Find "Organic Cotton T-Shirt" (PRD-003) - current stock: 8, threshold: 15
3. Click the "Simulate Sale" button
4. Watch as the stock decreases and triggers an INVENTORY_LOW event
5. New pricing and reorder suggestions will appear automatically
6. Review the AI-generated recommendations
7. Click "Accept" on the pricing suggestion to update the price
8. Click "Accept" on the reorder suggestion to increase stock

### Demand Spike Scenario (PRD-008)
1. Find "Hoodie — Heather Grey" (PRD-008)
2. Click "Simulate Sale" multiple times (3-4 times)
3. Watch the demand velocity increase
4. When velocity exceeds 3x the category average, a DEMAND_SPIKE event will trigger
5. See different recommendations with a DEMAND_SPIKE trigger badge
6. Accept the suggestions to adjust price and stock

## API Documentation

All APIs are documented in the backend README.md file.

## Troubleshooting

If you encounter any issues:

1. **Port conflicts**: Make sure ports 8080 (backend) and 5173 (frontend) are free
2. **Dependency issues**: Run `mvn clean install` in the backend directory
3. **Node modules issues**: Delete `node_modules` folder in frontend and run `npm install` again
4. **Database issues**: The application uses H2 in-memory database, so restarting will reset data

## Stopping the Applications

Press `Ctrl+C` in the terminal windows to stop both applications.