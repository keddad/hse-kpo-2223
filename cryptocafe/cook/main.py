import os
import psycopg2
import time

# i'm writing that at 01:00 of monday if it works it works

def main():
    username = os.environ["POSTGRES_USER"]
    password = os.environ["POSTGRES_PASSWORD"]
    host = os.environ["POSTGRES_HOST"]

    conn = psycopg2.connect(
        host = host,
        user = username,
        password = password
    )

    while True:
        time.sleep(1)

        cur = conn.cursor()

        cur.execute("SELECT ID FROM ORDERS WHERE STATUS = 'queued' ORDER BY ID")

        res_id = cur.fetchone()

        if res_id == None:
            continue

        res_id = res_id[0]
        time.sleep(5)

        cur.execute(f"UPDATE ORDERS SET STATUS = 'done' WHERE ID = {res_id}")

        print(f"Completed order {res_id}", flush=True)
        
        conn.commit()



main()