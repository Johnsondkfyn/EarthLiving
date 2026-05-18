INSERT INTO allocations (node_id, ip, ip_alias, port, server_id, created_at, updated_at)
VALUES (1, '159.195.149.253', 'earthliving-node', 8100, 3, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  ip_alias = VALUES(ip_alias),
  server_id = VALUES(server_id),
  updated_at = NOW();

SELECT id, node_id, ip, ip_alias, port, server_id
FROM allocations
WHERE port IN (25565, 8100)
ORDER BY port;
